test:
	./gradlew clean
	./gradlew test
buildImage:
	./gradlew clean
	./gradlew jibDockerBuild --image=todolist
run:
	docker run -it -p 8080:8080 todolist