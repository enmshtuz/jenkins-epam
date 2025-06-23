node {
    checkout([$class: 'GitSCM',
        branches: [[name: '*/main']],
        userRemoteConfigs: [[
            url: 'https://github.com/enmshtuz/jenkins-epam.git',
            credentialsId: 'github-creds'
        ]]    ])
    load 'Jenkinsfile'
}