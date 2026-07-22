# GitHub Workflow Guide for the b07group6 Project

This guide explains how to work with the b07group6 repository using Git and GitHub workflows.

## Initial Setup

Clone the b07group6 repository to your local machine:

```bash
git clone https://github.com/Abhinav-Kamatamu/b07group6.git
cd b07group6
```

## Standard Development Workflow

1. `git checkout <PARENT-BRANCH>` → `git pull origin <PARENT-BRANCH>`
2. `git checkout -b <YOUR-BRANCH-NAME>`
3. Make changes → `git add .` → `git commit -m "message"`
4. `git push -u origin <YOUR-BRANCH-NAME>`
5. Create PR on GitHub and move work item to Review
6. Request a reviewer
7. Get review approval
8. Move the item to Done on Jira after it has been reviewed
10. `git checkout <PARENT-BRANCH>` → `git pull origin <PARENT-BRANCH>`
11. Repeat for next task

Following this workflow ensures clean collaboration and maintains code quality across the b07group6 project.

## Detailed Steps for Standard Development Workflow

### 2. Pull Latest Changes from `<PARENT-BRANCH>`

Before starting any new work, always pull the latest changes from the branch you wish to work
off of (note for this project, we will mostly branch off of main, i.e. main is <PARENT-BRANCH>):

Switch to the `<PARENT-BRANCH>` on your local machine:
```bash
git checkout <PARENT-BRANCH>
```

Pull latest changes from remote (GitHub):
```bash
git pull origin <PARENT-BRANCH>
```

### 3. Create a New Feature Branch

Create a new branch following the naming convention based on your task:

#### Branch Naming Convention

#### Format: `<CATEGORY>/<JIRA-ID>-<SHORT-DESCRIPTION>`
Categories and their meaning:
- **feature** - For new features or functionalities
- **bugfix** - For fixing bugs in the code
- **refactor** - For improving code structure without changing functionality
- **test** - For writing or improving tests
- **doc** - For documentation updates
- **chore** - For routine configuration or environment tasks

**Jira Project ID:** Our project uses the key **B07**, so combine that with the ticket number on Jira when writing your Jira-ID (e.g., `B07-14`).

Examples:
- `feature/B07-14-artifact-home-page`
- `test/B07-22-login-presenter-tests`
- `bugfix/B07-5-firebase-null-pointer`
- `doc/B07-1-update-workflow-readme`

#### Create and Switch to New Branch

Create and switch to a new branch:
```bash
git checkout -b <YOUR-BRANCH-NAME>
```

Example:
```bash
git checkout -b feat/B07-14-artifact-home-page
```

### 4. Work on Your Changes

Make your code changes, commit them regularly:

Add changes to staging:
```bash
git add .
```

Commit with a descriptive message:
```bash
git commit -m "Add feature X"
```

*Always run `git status` before commiting to see what files you're about to commit.*
#### 4.5. When you're ready to push your changes to GitHub:

First update parent branch with the latest version from remote (GitHub):
```bash
git checkout <PARENT-BRANCH>
git pull
```

Return to your branch and merge parent branch into your branch:
```bash
git checkout <YOUR-BRANCH-NAME>
git merge <PARENT-BRANCH>
```

If there are no merge conflicts, everything should be committed, and you're ready to push to remote!
However, if conflicts do occur, follow the steps in the `mergeconflicts` folder to resolve them.

Push your branch to remote:
```bash
git push -u origin <YOUR-BRANCH-NAME>
```


### 5. Create a Pull Request (PR)

1. Go to the [b07group6 repository](https://github.com/Abhinav-Kamatamu/b07group6) on GitHub
2. Click **"Compare & pull request"** button (appears after pushing a new branch)
4. Select teammate reviewer
5. Click **"Create pull request"**

### 6. PR Review and Approval Process

#### For PR Authors:
- Ensure your code is well-tested and documented
- Respond to review comments promptly
- Make requested changes and push updates to the same branch

#### For Reviewers (if requested for PR):
- Review code thoroughly for:
  - Acceptance Criteria in story/task
  - Code quality and standards
  - Functionality and correctness
  - Documentation and comments
- Leave constructive feedback
- Approve when satisfied with the changes

#### Approval Process:
1. Reviewers examine the code changes
2. Leave comments or suggestions if needed
3. Click **"Approve"** when satisfied, move task to Done in Jira and merge branch
5. The branch will be merged into `<PARENT-BRANCH>`, so switch back to the parent branch locally and pull changes

### 7. After PR is Merged

Switch back to parent branch and pull the latest changes:

Switch back to parent branch:
```bash
git checkout <PARENT-BRANCH>
```

Pull the merged changes:
```bash
git pull origin <PARENT-BRANCH>
```

Delete the local feature branch (optional, but recommended):
```bash
git branch -d <YOUR-BRANCH-NAME>
```

## Branch Management Commands

### Useful Git Commands

See all branches:
```bash
git branch -a
```

Switch between branches:
```bash
git checkout <YOUR-BRANCH-NAME>
```

Check current branch and status:
```bash
git status
```

See commit history:
```bash
git log --oneline
```
See commit history graph:
```bash
git log --graph --all
```

See differences between branches:
```bash
git diff <BRANCH-1-NAME> <BRANCH-2-NAME>
```

### Switching Branches

Switch to main:
```bash
git checkout main
```

Switch to a feature branch:
```bash
git checkout <YOUR-BRANCH-NAME>
```

Create and switch to a new branch:
```bash
git checkout -b <NEW-BRANCH-NAME>
```

## Best Practices

1. **Always work on feature branches** - Never commit directly to `main`
2. **Keep branches up-to-date** - Regularly pull from `main` to avoid conflicts
3. **Use descriptive commit messages** - Help others understand your changes
4. **Small, focused PRs** - Easier to review and less likely to have conflicts
5. **Test before submitting** - Ensure your code works before creating a PR
6. **Reference story IDs** - Always reference the `B07-XX` task in branch names and PRs
7. **Clean up branches** - Delete merged branches to keep the repository tidy

## Troubleshooting

### Need Help?

- Check the [Git documentation](https://git-scm.com/doc)
- Ask Scrum Master or team-members working on similar tasks
- Use GitHub's built-in help and documentation
