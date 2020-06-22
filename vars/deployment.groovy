import org.test.yamlinjector
def call() {
podTemplate(label: 'mypod', containers: [
    containerTemplate(name: 'git', image: 'alpine/git', ttyEnabled: true, command: 'cat'),
    containerTemplate(name: 'maven', image: 'maven:3.3.9-jdk-8-alpine', command: 'cat', ttyEnabled: true),
    containerTemplate(name: 'docker', image: 'docker', command: 'cat', ttyEnabled: true ,envVars: [containerEnvVar(key: 'DOCKER_OPTS', value: '--insecure-registry="minikube.do:5000"')]),
     containerTemplate(name: 'kubectl', image: 'lachlanevenson/k8s-kubectl', command: 'cat', ttyEnabled: true),

  ],
  volumes: [
    hostPathVolume(mountPath: '/var/run/docker.sock', hostPath: '/var/run/docker.sock'),
    hostPathVolume(mountPath: '/.kube/config', hostPath: '/etc/kubernetes/admin.conf'),
  ]
  ) {
    

node('mypod') {
        
        stage('Clone repository') {
            container('git') {
                sh 'mkdir hello-world-war'
                sh 'whoami'
                sh 'hostname -i'
                sh 'git clone -b master https://github.com/bouda10/spring-boot-maven-example-helloworld hello-world-war'
				updateDeployment("bouda-deploy.yaml","nexus.do/bouda:latest")
            }
        }

        stage('Maven Build') {
            container('maven') {
                dir('hello-world-war/') {
                    sh 'hostname'
                    sh 'hostname -i'
                    sh 'mvn clean install'
                }
            }
        }
        
    stage('build image ') {
            container('docker') {
                dir('hello-world-war/') {
                    sh' docker login  nexus.do --username admin --password admin123  '
                    sh'docker build -t nexus.do/bouda:latest-${BUILD_NUMBER} .'
                    sh'docker tag   nexus.do/bouda:latest-${BUILD_NUMBER} nexus.do/bouda:latest '
                    sh 'docker push  nexus.do/bouda:latest-${BUILD_NUMBER}'
                }
            }
        }
        
    stage('deploy image ') {
           container('kubectl') {
                dir('hello-world-war/') {
                sh 'kubectl apply -f bouda-deploy.yaml '  
                 sh' kubectl expose deployment bouda --type=NodePort --port=8080 -n dev' 
                
            }
        }}
    }
	}
}