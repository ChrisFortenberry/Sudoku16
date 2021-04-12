#!/usr/bin/env
import string
import sys
import math
import random
import time

def cross(A, B):
    return [a+b for a in A for b in B]

def parse_grid(grid):
    """Convert grid to a dict of possible values, {square: digits}, or
    return False if a contradiction is detected."""
    ## To start, every square can be any digit; then assign values from the grid.
    values = dict((s, digits) for s in squares)
    for s,d in grid_values(grid).items():
        if d in digits and not assign(values, s, d):
            #print(d, s)
            return False ## (Fail if we can't assign d to square s.)
    return values

def verify(values):
    for u in unitlist:
        freq = {}
        for s in u:
            if values[s] in freq: freq[values[s]] += 1
            else: freq[values[s]] = 1
            if len(values[s]) < freq[values[s]]:
                return False
        for k in freq:
            if len(k) == freq[k] and len(k) > 1:
                for s in u:
                    if values[s] in k:
                        return False
    return True

def grid_values(grid):
    "Convert grid into a dict of {square: char} with '.' for empties."
    chars = [c for c in grid if c in digits or c in '.']
    #print(chars)
    assert len(chars) == board_size ** 2
    return dict(zip(squares, chars))

def assign(values, s, d):
    """Eliminate all the other values (except d) from values[s] and propagate.
    Return values, except return False if a contradiction is detected."""
    other_values = values[s].replace(d, '')
    if all(eliminate(values, s, d2) for d2 in other_values):
        #print(values)
        return values
    else:
        return False

def eliminate(values, s, d):
    """Eliminate d from values[s]; propagate when values or places <= 2.
    Return values, except return False if a contradiction is detected."""
    if d not in values[s]:
        return values ## Already eliminated
    values[s] = values[s].replace(d,'')
    ## (1) If a square s is reduced to one value d2, then eliminate d2 from the peers.
    if len(values[s]) == 0:
        return False ## Contradiction: removed last value
    elif len(values[s]) == 1:
        d2 = values[s]
        if not all(eliminate(values, s2, d2) for s2 in peers[s]):
            return False
    ## (2) If a unit u is reduced to only one place for a value d, then put it there.
    for u in units[s]:
        dplaces = [s for s in u if d in values[s]]
        if len(dplaces) == 0:
            return False ## Contradiction: no place for this value
        elif len(dplaces) == 1:
        # d can only be in one place in unit; assign it there
            if not assign(values, dplaces[0], d):
                return False
    return values

def display(values):
    "Display these values as a 2-D grid."
    width = 1+max(len(values[s]) for s in squares)
    line = '+'.join(['-'*(width*board_size_root)]*board_size_root)
    for r in rows:
        print(''.join(values[r+c].center(width)+('|' if c in cols[board_size_root-1:board_size-1:board_size_root] else '')
                      for c in cols))
        if r in rows[board_size_root-1:board_size-1:board_size_root]: print(line)
    print('\n')

def display_empty(values):
    vals = values.copy()
    for s in squares:
        if len(vals[s]) > 1: vals[s] = '.'
    display(vals)

def solve(grid):
    global already_seen_boards
    already_seen_boards = {}
    global solutions
    solutions = {}
    global iterator
    iterator = 0
    global dfs_path
    dfs_path = []
    
    return search(parse_grid(grid))

def search(values):
    "Using depth-first search and propagation, try all possible values."
    global iterator
    global dfs_path
    iterator += 1
    print("Search iter %i" % iterator)
    if values is False:
        return False ## Failed earlier
    # display_empty(values)
    if all(len(values[s]) == 1 for s in squares): 
        return values ## Solved!
    global already_seen_boards
    if puzzle_to_string(values) in already_seen_boards: 
        #print("Already seen")
        return False
    else: 
        #print("Not seen!")
        already_seen_boards[puzzle_to_string(values)] = values
    # display(values)
    ## Chose the unfilled square s with the fewest possibilities
    n,s = min((len(values[s]), s) for s in squares if len(values[s]) > 1)
    # for e in (search(assign(values.copy(), s, d)) for d in values[s]):
    #     if e: 
            
    # return some(search(assign(values.copy(), s, d))
    #             for d in values[s])
    # for e in (search(assign(values.copy(), s, d)) for d in values[s]):
    #     if e: return e
    # return some(search(assign(values.copy(), s, d))
    #             for d in values[s])
    for d in values[s]:
        # print()
        # display_dfs_path()
        # print("Trying %s in square %s; previous value was %s" % (d, s, values[s])) 
        trying_board = assign(values.copy(), s, d)
        if trying_board:
            dfs_path.append((s, d))
            # display(trying_board)
            # time.sleep(1)
            e = search(trying_board)
            if e: return e
            else: 
                dfs_path.pop()
                # print("Backing up")
                # if not dfs_path: time.sleep(3)
        # else: 
        #     print("Could not assign %s to square %s" % (d, s))
        
    return False

# This function is put in the above because of recursion problems for 25x25 boards, but is included for posterity.
def some(seq):
    "Return some element of seq that is true."
    for e in seq:
        if e: return e
    return False

def display_dfs_path():
    global dfs_path
    print(' -> '.join(str('%s @ %s' % (d, s)) for s,d in dfs_path))

def random_puzzle(N=17):
    """Make a random puzzle with N or more assignments. Restart on contradictions.
    Note the resulting puzzle is not guaranteed to be solvable, but empirically
    about 99.8% of them are solvable. Some have multiple solutions."""
    global already_generated_boards
    values = dict((s, digits) for s in squares)
    for s in shuffled(squares):
        if not assign(values, s, random.choice(values[s])):
            break
        ds = [values[s] for s in squares if len(values[s]) == 1]
        if len(ds) >= N and len(set(ds)) >= board_size-1:
            if all(len(values[s]) == 1 for s in squares): break ## We don't want an already solved one
            if not verify(values): break ## We don't want an invalid one either
            puzzle_string = puzzle_to_string(values)
            if puzzle_string in already_generated_boards: break ## We've already heard this joke
            already_generated_boards.add(puzzle_string)
            return puzzle_string
    return random_puzzle(N) ## Give up and make a new puzzle

def shuffled(seq):
    "Return a randomly shuffled copy of the input sequence."
    seq = list(seq)
    random.shuffle(seq)
    return seq

# def search_unique2(values):
#     if values is False:
#         return False

#     if all(len(values[s]) == 1 for s in squares):
#         return values

#     for s in squares if len(values[s]) > 1:
#         for e in (search(assign(values.copy(), s, d)) for d in values[s]):
#             if e: solutions[puzzle_to_string(e)]

def solve_unique(grid):
    global already_seen_boards
    already_seen_boards = {}
    global solutions
    solutions = {}
    global iterator
    iterator = 0
    global dfs_path
    dfs_path = []
    
    return search_unique(parse_grid(grid))

def search_unique(values):
    "Modified search()"
    global solutions
    global iterator
    if values is False:
        return False ## Failed earlier
    if not verify(values):
        return False ## Invalid board
    # if len(solutions) > 1:
    #     return False ## Get out of here ASAP
    #display_empty(values)
    if all(len(values[s]) == 1 for s in squares):
        return values ## Solved!
    
    ## Chose the unfilled square s with the fewest possibilities
    # n,s = min((len(values[s]), s) for s in squares if len(values[s]) > 1)
    
    for n,s in sorted((len(values[s]), s) for s in squares if len(values[s]) > 1):
    #     for d in values[s]: 
    #         search_unique(assign(values.copy(), s, d))
    #         if len(solutions) > 1:
    #             break
    #     if len(solutions) > 1:
    #             break
        for e in (search(assign(values.copy(), s, d)) for d in values[s]):
            if e: solutions[puzzle_to_string(e)] = e
            if len(solutions) > 1: return False
            # if e:
            #     iterator += 1
            #     #print("search_unique loop %i" % iterator)
            # if e and not first_solution: first_solution = (puzzle_to_string(e), e) #solutions[puzzle_to_string(e)] = e
            # elif e and first_solution[0] != puzzle_to_string(e): return False
        if len(solutions) != 1: break
    # while True:
    #     solution = some(search(assign(values.copy(), s, d))
    #             for d in values[s])
    #     if not solution: break
    #     solution_string = puzzle_to_string(solution)
    #     if solution_string in solutions: break
    #     solutions[solution_string] = solution
    #print(len(solutions))
    #return False if len(solutions) != 1 else solutions.popitem()[1]
    #if first_solution: print(first_solution)
    # print(solutions)
    return solutions.popitem()[1] if solutions else False


def puzzle_to_string(values):
    #print(values)
    return ''.join(values[s] if len(values[s])==1 else '.' for s in squares)

sys.setrecursionlimit(10**6)
board_size = 9
if board_size > 9:
    digits = '0123456789' + string.ascii_uppercase[:board_size-10] #'0123456789ABCDEF'
else:
    digits = '123456789'
rows = string.ascii_lowercase + 'αβγδεζηθικ'
rows = rows[:board_size]
cols = digits
row_slices = []
col_slices = []
board_size_root = math.isqrt(board_size)
for i in range(board_size_root):
    row_slices.append(rows[board_size_root*i:board_size_root*(i+1)])
    col_slices.append(cols[board_size_root*i:board_size_root*(i+1)])
squares = cross(rows, cols)
unitlist = ([cross(rows, c) for c in cols] +
            [cross(r, cols) for r in rows] +
            [cross(rs, cs) for rs in row_slices for cs in col_slices])
units = dict((s, [u for u in unitlist if s in u]) 
             for s in squares)
peers = dict((s, set(sum(units[s],[]))-set([s]))
             for s in squares)

#grid = '0.A...C5..1......7.B....4.0.5..6...C.6.D9..7281.D..172......9.....56..7.3....2.....D.1........B..81FE..3C.42.....32..D..B8.91..F...E.F..8..0D.3......5.....BF.C..09.6C..57....A.3A.2..8.6.E.B....CD.17...F....641.6.59A......E.....3..F.D52....B.F.5.8BC...A....'
# solved_grid = solve(random_puzzle())
# if solved_grid:
#     display(solved_grid)
#     #display_empty(parse_grid(solved_grid))
# else:
#     print("Couldn't find a solution I guess!")

i = 1
gen_time = []
solve_time = []
long_grids = []
already_generated_boards = set()
while True:
    start = time.time()
    random_grid = random_puzzle()
    gen_time.append(time.time()-start)
    start = time.time()
    solution = solve_unique(random_grid)
    t = time.time()-start
    solve_time.append(t)
    if t > 0.1: long_grids.append((t, random_grid))
    if solution:
        display_empty(parse_grid(random_grid))
        display(solution)
        print(random_grid)
        break
    i += 1
    print(i)
    
print("Average gen time: %.3f secs; Average solve time: %.3f secs" % (sum(gen_time)/i, sum(solve_time)/i))
print("Max gen time: %.3f secs; Max solve time: %.3f secs" % (max(gen_time), max(solve_time)))

## Testing the uniqueness checker
## Grid 1 has multiple, grid 2 doesn't
# grid = '.81.........8........6......5.....9......9.28.3..7.1........54....4...3.........6'
# grid2 = '.5...3..61..4...7...2.9.4..3..7...4...8.1.....6...9..5....379.....2...3......8..2'
# grid_sol = solve_unique(grid)
# grid2_sol = solve_unique(grid2)

# print("GRID 1")
# display_empty(parse_grid(grid))
# if grid_sol: display(grid_sol)
# else: print("Grid1 is not unique")

# print("GRID 2")
# display_empty(parse_grid(grid2))
# if grid2_sol: display(grid2_sol)
# else: print("Grid2 is not unique")

## Grids with no solution
# print(solve_unique('.............87.............6.8........7925...34156..8.....................9..61.'))
# print(solve_unique('.9.3........2..........7....6.4..8...4.........5......432...7...7..6.......78....'))
# print(solve_unique('.2.......1..4..........2......9........52..1.............7...8.582.6....6..285...'))
# print(solve('...54...8............2.......79...............63.1.7......7.1..745....92.........'))