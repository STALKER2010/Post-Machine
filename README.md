# Post Machine
## Description
Fully-featured IDE for Post-Turing machine.

## Features
* Code Editor
* Post VM
* Debugger
* Lint tool
* Line editor

## Language
1/M - Mark current line position
0/X - Unmark current line position
< and > - Move line position
! - Stop execution
IF/? - Conditional operator

## My extensions to language
VM contains one int register. So we have commands:
-- and ++ - Decrement and increment register
SET - Set register to given value

## IF operator
### First part
#### Work with registers
? =5
If register value equals 5...
? ~5
If register value is not 5…
? <5
If register value is less than 5…
? <=5
If register value is less or equal 5…
And so like these:
? >5
? >=5

#### Work with line
It's just:
?
It's true when line at current position is marked, false otherwise.

### Second part
#### Two numbers
Goto line <second> if first part is true
Goto line <first> if first part is false

#### One number
Goto line <number> if first part is true
Goto next line if first part is false

## Dependencies
The project depends on AppCompat support library and (temporarily) LeakCanary.

## Authors and dates
Copyright © Andrew Boyarshin (STALKER_2010) 2014-2015
Project was under active development about 6 days: 2 days in August 2014 and 4 days in May 2015.

