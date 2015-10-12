## Synopsis

Este é um exemplo de um chat de bate papo simples usando JAVA RMI. 

## Como compilar o código fonte ?

Para compila o código fonte basta executar o comando abaixo redirecionando para a pasta chamada "out" :

    user> javac src/*.java -d out/

## Como executar as aplicações ?

A aplicação servidora, por podrão, está configurada para executar com o IP local (127.0.0.1) na porta 1099. O novo do servidor no resgistry é "ChatServer". Assim, para executar a aplicação servidora, basta executar o comando abaixo dentro da pasta "out":

    user> java ChatServerImp

Para inicializa a aplicação cliente será necessário passar como argumento para aplicação o endereço IP, porta e apelido do cliente. Por exemplo, é possivel inicializar o cliente da seguinte forma:

    user> java ChatClientImp 127.0.0.1 1091 joao_paulo

