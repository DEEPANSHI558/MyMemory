package com.example.mymemory

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymemory.models.BoardSize
import com.example.mymemory.models.MemoryCard
import kotlin.math.min

//numpieces representing how many memory cards there are

class MemoryBoardAdapter(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
    //companion objects are used to define some constants
 companion object{
     private const val MARGIN_SIZE=10
        private val TAG="MemoryBoardAdapter"
 }
    //we construct this interface as whoever constructs this memoryBoardAdapter it will be their responsibilty to pass in an instance of this interface

    interface CardClickListener{
        fun onCardClicked(position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //here parent is the recycler view
        val cardWidth=parent.width/boardSize.getWidth()-(2* MARGIN_SIZE)
        val cardHeight=parent.height/boardSize.getHeight()-(2* MARGIN_SIZE)
        val cardSideLength=min(cardWidth,cardHeight)
        //for margin first we have to make space in the height and width
      val view=LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false)
        val layoutParams=view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width=cardSideLength
        layoutParams.height=cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
  }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.bind(position)
    }

    override fun getItemCount(): Int {
       return boardSize.numCards
    }
    //we will define our own View holder
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        private val imageButton=itemView.findViewById<ImageButton>(R.id.imageView)
        fun bind(position: Int) {
            val memoryCard=cards[position]
        imageButton.setImageResource(
                if(cards[position].isFaceUp)
                    cards[position].identifier
            else
                R.drawable.ic_launcher_background
            )
            imageButton.alpha=if(memoryCard.isMatched) .4f else 1.0f
            val colorStateList=if(memoryCard.isMatched) ContextCompat.getColorStateList(context,R.color.color_gray)else null
            ViewCompat.setBackgroundTintList(imageButton,colorStateList)//to set background of shading to the image button
          imageButton.setOnClickListener{
//              Log.i(TAG,"Clicked on position $position")
              cardClickListener.onCardClicked(position)
          }
        }
    }


}
