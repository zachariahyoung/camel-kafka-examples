# camel-kafka-examples

## Start Confluent Platform in Docker
1. Run git clone on the following repo https://github.com/confluentinc/cp-all-in-one/tree/6.2.1-post/cp-all-in-one
2. Navigate to your locally cloned folder.
3. Run _docker-compose up -d_ in the command prompt
4. Navigate to http://localhost:9021 to view the Control Center and interact with your local cluster via GUI. 9021 is the Confluent Platform's default port.

## Message Flow

<h1 align="center">
  <a href="https://github.com/jeffreytse/jekyll-spaceship">
    <img alt="producer" src="https://raw.githubusercontent.com/zachariahyoung/camel-kafka-examples/master/images/diagram.jpg" width="400">
  </a>
  
</h1>

## Start producer

### How to run

You can run the example producer using.  Make sure to navigate to the producer folder first.

    mvn spring-boot:run

Next you can use the Control Center to confirm records are being created.

<h1 align="center">
  <a href="https://github.com/jeffreytse/jekyll-spaceship">
    <img alt="producer" src="https://raw.githubusercontent.com/zachariahyoung/camel-kafka-examples/master/images/producer.PNG" width="600">
  </a>
  
</h1>

## Start Consumer

### How to run

You can run the example producer using.  Make sure to navigate to the consumer folder first.

    mvn spring-boot:run

Review the log to see messages being printed.


