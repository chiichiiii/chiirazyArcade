package com.smu8.game;

import java.util.List;
import java.util.Random;

class WordManager {
   private final List words = List.of("java", "object", "class", "method", "inheritance", "interface", "encapsulation", "polymorphism", "exception", "package");
   private final Random random = new Random();

   public String getRandomWord() {
      return (String)this.words.get(this.random.nextInt(this.words.size()));
   }
}
