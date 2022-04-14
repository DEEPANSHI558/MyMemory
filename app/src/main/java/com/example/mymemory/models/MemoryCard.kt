package com.example.mymemory.models
//objective here is we have to list out ecvery aspect of the memory card

data class MemoryCard(
    val identifier:Int,
    var isFaceUp:Boolean=false,
    var isMatched:Boolean=false
)
