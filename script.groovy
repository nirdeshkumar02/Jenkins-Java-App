def buildApp() {
    echo "Building Your Application..."
}

def testApp() {
    echo "Tesing Your Application..."
}

def deployApp() {
    echo 'deploying the application...'
    echo "deploying version ${params.VERSION}"
} 

return this
