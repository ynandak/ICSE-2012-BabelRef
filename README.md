# ICSE-2012-BabelRef
Detection and Renaming Tool for Cross-Language Program Entities in Dynamic Web Applications

This repository is contains information related to the tool BabelRef presented in the 34th International Conference on Software Engineering (ICSE), 2012. The tool was originally presented in [this paper](http://ieeexplore.ieee.org/xpl/login.jsp?tp=&arnumber=6227240).

This repository _is not_ the original repository for this tool. Here are some links to the original project:
* [The Official Project Page, not including source code](http://home.engineering.iastate.edu/~hungnv/Research/BabelRef/)
* [A Video of the Tool](http://home.engineering.iastate.edu/~hungnv/Research/BabelRef/?page=tool_demo)

In this repository, for ExampleTool you will find:
* :white_check_mark: Source code
* :white_check_mark: The original tool
* :white_check_mark: A slightly modified version of the tool [Mohammed H Hassan](https://github.com/mhhassan) got working

This repository was constructed by [Mohammed H Hassan](https://github.com/mhhassan) under the supervision of [Emerson Murphy-Hill](https://github.com/CaptainEmerson). Thanks to [Hung Viet Nguyen](http://home.engineering.iastate.edu/~hungnv/Personal/) for his help in establishing this repository. 

## Instructions

This tool has been distributed as a collection of Eclipse plugin projects, that can be imported into Eclipse and built. The repository's root contains the following folders:
- __Source__: 5 (five) Eclipse projects for this tool.
- __Binaries_: A collection of JAR files usable as is for use in an Eclipse installation.

### Description of /projects
Contains the following Eclipse projects:
- __Data Model__: Symbolic executor for PHP
- __edu.iastate.hungnv.babelref__: Eclipse plugin for showing embedded entities and dangling references (provided by edu.iastate.hungnv.babelref.ui.views.ERefEntityView)
- __Html Partial Parser__: Parser for the output of symbolic execution
- __Util__: Util libraries used by the other projects
- __Web Entities__: Detection of embedded entities and dangling references

## Attribution

Author of tool: Hung Viet Nguyen
 
Personal website: http://home.engineering.iastate.edu/~hungnv/Personal/index.php
