mvn deploy:deploy-file -DgroupId=io.mateu.erp \
  -DartifactId=apisha256 \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -Dfile=apiSha256.jar \
  -DrepositoryId=mateu-central \
  -Durl=http://nexus.mateu.io/repository/mateu-central/


mvn deploy:deploy-file -DgroupId=io.mateu.erp \
  -DartifactId=bcprov \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -Dfile=bcprov-jdk15on-1.4.7.jar \
  -DrepositoryId=mateu-central \
  -Durl=http://nexus.mateu.io/repository/mateu-central/

mvn deploy:deploy-file -DgroupId=io.mateu.erp \
  -DartifactId=codec \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -Dfile=commons-codec-1.3.jar \
  -DrepositoryId=mateu-central \
  -Durl=http://nexus.mateu.io/repository/mateu-central/

mvn deploy:deploy-file -DgroupId=io.mateu.erp \
  -DartifactId=json \
  -Dversion=1.0 \
  -Dpackaging=jar \
  -Dfile=org.json.jar \
  -DrepositoryId=mateu-central \
  -Durl=http://nexus.mateu.io/repository/mateu-central/