ifeq ($(OS),Windows_NT)
    gradle_script := gradlew
else
    gradle_script := ./gradlew
endif

clean:
	$(gradle_script) clean

test: clean
	$(gradle_script) test

gradle-run: clean
	$(gradle_script) bootRun

build-image: clean
	$(gradle_script) jibDockerBuild --image=todolist

run: build-image
	docker run -it -p 8080:8080 todolist