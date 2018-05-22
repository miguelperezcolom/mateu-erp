mvn deploy:deploy-file -DgroupId=org.easytravelapi \
  -DartifactId=core \
  -Dversion=0.1.20 \
  -Dpackaging=jar \
  -Dfile=core-0.1.20.jar \
  -DrepositoryId=mateu-central \
  -Durl=http://nexus.mateu.io/repository/mateu-central/