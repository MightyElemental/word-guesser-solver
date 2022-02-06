# About
This is not ascociated with the game in any official sense. I made this as a side project for fun.

# How to use
## Pre-requisites
- Java 11
- Access to a command line

## Setup
1. Download the jar file (or build from source)
2. Download a dictionary file\*

\*For the dictionary, I selected the 'ENGLISH - 194,000 words TXT' option from the following website: http://www.gwicks.net/dictionaries.htm

## Usage

Each argument consists of a word and the result of the word, delimited by a colon. For example, lets say you entered the word ``crane`` and you found the letters ``c`` and ``e`` were correct and ``a`` was in the word but the wrong place, you would enter this as the following: ``'crane:c!?!e'`` (including the single quotes)

- ``?`` indicates a correct letter in the wrong place
- ``!`` indicates the letter is not in the word
- ``Any letter a-z`` indicates the letter was correct and in the correct place

### Example usage:

```bash
> java -jar wordlesolve.jar 'crane:c!?!e'
Loaded 194433 words
Processed input - crane:c!?!e
Excluded: [r, n]
Included: {a=[wp=[2]], c=[wp=[-1]], e=[wp=[-1]]}
Known: c---e
Potential words: 13
---

-= POSSIBLE WORDS =-
cache
cadie
comae
cadge
cadee
caste
cause
cavie
coxae
caese
cavae
cable
calve
```