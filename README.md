## Sudoku16
Sudoku16 is a side project I've been working on in an attempt to create a nice 16x16 sudoku game to accompany the 9x9 one I already have (not by me). I couldn't find one (easily) so I decided to just try and make it and it's been an interesting journey into android development (and some python, more on that in a moment). It's not really done yet but I figured I should start tracking on git too just to help train that skill.

The android app part comprises the majority of the app and its functionality. Right now it features note-taking and highlighting, though ideally in the future the highlighting should be an option (I just like playing with it on usually). It also doesn't feature any actual grids yet (again, more on that in the python area) It is written in Kotlin and was originally following [Patrick Feltes'](https://www.youtube.com/watch?v=o6P05m0E9z4) tutorial on the subject, though restructured for 16x16 (and potentially other sizes?) and gone further since the tutorial somewhat abruptly ended (as of this time of writing, 4/12/21).

Now, the python script included in this repository exists as an attempt to make a sudoku puzzle generator. After reading a few pages on the subject, I ended up following [Peter Norvig's essay](http://norvig.com/sudoku.html) (though again, with modifications). I haven't included everything he wrote and the code is a good bit messier, but it is meant to be able to generate (and solve) puzzles up to 36x36. That said it's been an interesting experience attempting to reconfigure the program so that it would ideally only generate puzzles with 1) a solution and 2) *only* one solution. I am sure there is a better way to do this, but I haven't gotten to the point of refactoring everything or trying a different language. Maybe in the future.

### Todo (though not in any real order):
- Generate a good chunk of puzzles so as to give the game variety. I'm aware of the idea that a single puzzle can be used as a "seed" due to being able to be rotated, flipped, etc. while still maintaining validity, which is helpful. I'll probably make some sort of equality checker to make sure I don't accidentally have two identical seeds.
-- Also, I want to figure out how I should judge difficulty exactly... I've seen different thoughts on the subject, I just need to figure out which one I want to implement.
- On that note, I need to make a main menu of sorts so that a new game can be started.
- I would also like to implement a timer w/ local leaderboard.
- Different sizes in the app? I have some concerns about how 25x25 or 36x36 would fit on a phone screen, unfortunately.
- Oh speaking of, there was a slight UI issue I noticed with regards to how the board draws... I would like to fix that.
- Settings, with toggleable helpers
- A light theme perhaps, to accompany the existing dark theme