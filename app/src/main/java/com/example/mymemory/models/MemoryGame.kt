package com.example.mymemory.models

import com.example.mymemory.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize:BoardSize)
{


    val cards: List<MemoryCard>
    var numPairsFound=0
    private var numCardFlipped=0
    private var indexOfSingleSelectedCard:Int?=null
    init{
        val chosenImages= DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomImages=(chosenImages+chosenImages).shuffled()
        //we will first change all the chosen images into a map of the to a memory car,
        cards= randomImages.map{MemoryCard(it)}
    }
    //game logic we get the reference of the card we clicked and then we flip the
    fun flipCard(position: Int):Boolean {
         numCardFlipped++
         val card=cards[position]
         var foundMatch=false
        //Three cases (valid)
        //0 cards that are previously flipped over=>restore+simply filp over the selected card
        //1 card previously flipped over=>flip over the selected card and if they match
        //2 cards previously flipped over=>restore the cards that were previously flipped over but was not a match+flip over the selected card
        if(indexOfSingleSelectedCard==null) { //0 or 2 cards are flipped over
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            foundMatch= checkForMatch(indexOfSingleSelectedCard!!,position)
            indexOfSingleSelectedCard=null
        }
          card.isFaceUp=!card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int) :Boolean{
         if(cards[position1].identifier!=cards[position2].identifier){
             return false
         }
        cards[position1].isMatched=true
        cards[position2].isMatched=true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for(card in cards) {
            if(!card.isMatched) {
                card.isFaceUp=false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound==boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp

    }

    fun getNumMoves(): Int {
          return numCardFlipped/2
    }
}