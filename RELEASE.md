# Upgrade and release procedure

1. upgrade RxJava project to next RxJava release (do all this steps in rxjava project)
    1. create a new branch at last gwt branch using the next RxJava version tag as branch name 
    2. rebase this branch into the last RxJava tag
    3. upgrade the gradle script to indicate the new tag version (used to calculate changed files) and fixup the script commit
    4. use gwtCompile, if it fails create new super sources or modify RxJava code until it compiles, commit the changes in the one of the existing commit
    5. finally, remove content of `src/main/modified` (in this project) and use gwtCopy to update push the new changes
2. Apply the patch `src/main/Schedulers`
3. verify the project
4. commit `Upgrade to RxJava X.Y.Z` 
5. call `$ release.sh X.Y.Z-gwt1` (if a hotfix need to be made use gwt2, gwt3, etc)   
