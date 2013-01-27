make -C Implementation/lib/svm_light/ --no-print-directory --silent
java -jar Implementation/build/jar/svm.jar DATA.txt Implementation/CONF.txt
