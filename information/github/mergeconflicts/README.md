# Merge Conflicts Guide

Android Studio has a very nice system for resolving merge conflicts, which we will use as follows:

Whenever a merge conflict occurs, a green button should appear in Android Studio with the
hover text "Resolve Conflicts.. ". Press that button.

Android Studio will show you all the files with conflicts,
highlighting the problematic lines. Resolve all the highlighted lines in all
conflicting files. When you've done so, a pop-up button will appear with the text "Save changes
and finish merging".

Press that and you're done merging!

Finally, run:
```bash
git add .
git commit
```

And you're ready to push to remote.

If you're still confused, watch this video for a hands-on demonstration:
[Merging - Git for Android Developers - Part 5](https://www.youtube.com/watch?v=T-lh2YMXO6g&t=233s&pp=ygUdbWVyZ2UgY29uZmxpY3QgYW5kcm9pZCBzdHVkaW8%3D)