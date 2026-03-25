deploy:
	./mvnw clean deploy -B -Prelease --file library/pom.xml
.PHONY: deploy

show-versions:
	./mvnw versions:display-dependency-updates  versions:display-plugin-updates -ntp --file library/pom.xml
.PHONY: show-versions

build:
	./mvnw clean install --file library/pom.xml

demo-full: build
	export DEMO_ENV="env_value" && ./mvnw -q exec:java --file examples/full-demo/pom.xml
.PHONY: demo-full