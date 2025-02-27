stage('Push APK to GitHub') {
    steps {
        script {
            // Configure Git user (use GitHub Actions bot)
            bat 'git config --global user.name "github-actions[bot]"'
                        bat 'git config --global user.email "github-actions[bot]@users.noreply.github.com"'

            // Move to workspace
           // sh 'cd $WORKSPACE'

            // Add APK file to Git
            bat 'git add app/build/outputs/apk/debug/app-debug.apk'

            // Commit the change
            bat 'git commit -m "Add built APK to develop branch" || echo "No changes to commit"'

            // Push to GitHub
            bat 'git push origin develop'

        }
    }
}