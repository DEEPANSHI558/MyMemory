package com.example.mymemory

import android.animation.ArgbEvaluator
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryCard
import com.example.mymemory.models.MemoryGame
import com.example.mymemory.utils.DEFAULT_ICONS
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object{
        private const val TAG="MainActivity"
    }
    private lateinit var clRoot: ConstraintLayout
    private lateinit var memoryGame:MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var moves:TextView
    private lateinit var pair:TextView
    private lateinit var adapter:MemoryBoardAdapter
    private var boardSize:BoardSize= BoardSize.HARD//default value
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //we get a reference to those views by findViewById
        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        moves = findViewById(R.id.moves)
        pair = findViewById(R.id.pairs)
        pair.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        //now getting the default icons that we will show in our game
        //we are going to randomize the list and then take a particular number according to the getNumPairs to choose only that many icons and then double it as we have to choose the pairs
        setUpBoard()
    }
    private fun setUpBoard() {
        when(boardSize){
            BoardSize.EASY -> {
                moves.text = "Easy 4 x 2"
                pair.text = "Pairs 0/4"
            }
            BoardSize.MEDIUM -> {
                moves.text = "Medium 6 x 3"
                pair.text = "Pairs 0/9"
            }
            BoardSize.HARD -> {
                moves.text = "Hard 6 x 6"
                pair.text = "Pairs 0/12"
            }
        }
        pair.setTextColor(ContextCompat.getColor(this, R.color.color_progress_none))
        //now getting the default icons that we will show in our game
        //we are going to randomize the list and then take a particular number accor
        memoryGame= MemoryGame(boardSize)
        adapter= MemoryBoardAdapter(this,boardSize,memoryGame.cards,object:MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })
        rvBoard.adapter=adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager=GridLayoutManager(this,boardSize.getWidth())
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh->{
                //set up the game again
                if(memoryGame.getNumMoves()>0&&!memoryGame.haveWonGame()){
                    showAlertDialog("Quit your current game",null,View.OnClickListener {
                        setUpBoard()
                    })
                }
                else {
                    setUpBoard()
                }
                return true
            }
            R.id.mi_newsize->{
                showNewSizeDialog()
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun showNewSizeDialog() {
        val boardSizeView=LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize=boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize)
        {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD ->radioGroupSize.check(R.id.rbHard)
        }
        showAlertDialog("Choose a new Size",boardSizeView,View.OnClickListener {
            //set a new value for the board size
            boardSize=when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy->BoardSize.EASY
                R.id.rbMedium->BoardSize.MEDIUM

                else -> BoardSize.HARD
            }
            setUpBoard()

        })
    }

    private fun showAlertDialog(title:String,view: View?,positiveClickListener:View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel",null)
            .setPositiveButton("Ok"){
                _,_->
                positiveClickListener.onClick(null)

            }.show()

    }


    private fun updateGameWithFlip(position: Int) {
        //Error handling
        if(memoryGame.haveWonGame()){
            //Alert the user of the invalid move
            Snackbar.make(clRoot,"You already Won!",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            //Alert the user of an invalid move
            Snackbar.make(clRoot,"Invalid Move!",Snackbar.LENGTH_LONG).show()
            return
        }
           if( memoryGame.flipCard(position)){//if this is true
               val color=ArgbEvaluator().evaluate(
                   memoryGame.numPairsFound.toFloat()/boardSize.getNumPairs(),
                   ContextCompat.getColor(this,R.color.color_progress_none),
                   ContextCompat.getColor(this,R.color.color_progress_full)
               )as Int
               pairs.setTextColor(color)
//               Log.i(TAG,"Found a match!Num pairs Found: ${memoryGame.numPairsFound}")
               pair.text="Pairs : ${memoryGame.numPairsFound}/${boardSize.getNumPairs()}"
               if(memoryGame.haveWonGame()){
                   Snackbar.make(clRoot,"You Won! Congratulations",Snackbar.LENGTH_LONG).show()
               }
           }
         moves.text="Moves :${memoryGame.getNumMoves()}"
            adapter.notifyDataSetChanged()

    }
}