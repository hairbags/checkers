=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: _lelyukh_
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Testable Components: Checkers server model and BoardSquare class are both testable components.

  2. File I/O: game save and load functions: You can continue games after closing the
  program by pressing load the next time you play.

  3. 2D Arrays: BoardSquare[][] board is a 2D array that stores the state of the entire checkerboard.
  This is the most appropriate way to store a grid board.

  4. Collections: LinkedList of past board states to enable undo function. This is the correct use
  because turns are sequentially linked from first to last.

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.
  
BoardSquare: stores the data in a given square on the board, tells you whether the square is light or dark,
has a checker or not, whether the checker is black or is a king if it is there.

Checkers: server model of game. Initializes and stores the state of the entire board and provides 
comprehensive method called selectSquare that selects a square on the board and handles all interactions;
it can choose a checker then choose where to move it, it can choose a checker then choose where to kill, 
it can select an invalid square and do nothing, it handles all possible user interaction with the
server model. It can also return information about the state of the game for game state labels.

CheckerBoard: JPanel that draws the state of the board after every turn and implements mouse listener
to interact with the board. Initializes a Checkers server model.

Instruct: JPanel with game instructions.

RunCheckers: Implements Runnable. Creates a CheckerBoard and all of the necessary buttons. Shows the 
GUI.

- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?
  
  Before I added the multiKTurn and killTurn private boolean variables it was really hard to keep track
  of how I was supposed to control which moves are possible.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

I think there is a good separation of functionality. Private state of Checkers is unchangeable(I hope) by 
outside methods, they can only play the game.

========================
=: External Resources :=
========================

- Cite any external resources (libraries, images, tutorials, etc.) that you may
  have used while implementing your game.
  
  Javadocs. I used the Point datatype extensively.
