# Unit 7 - Online Chat Application

## Overview

This project is a simple text-based chat application written
in Java. A central server accepts multiple clients, assigns
unique user IDs, and broadcasts messages to connected users.

## Requirements

- Java Development Kit (JDK)
- GitHub Codespaces or another Java development environment
- A terminal

## Compile the Application

Run this command inside the project folder:

javac Unit7.java ChatServer.java ChatClient.java

## Start the Server

Run this command in the first terminal:

java Unit7 server

The default port is 5000.

## Start the Clients

Open a second terminal and run:

java Unit7 client

Open a third terminal and run the same command:

java Unit7 client

Enter a username when prompted.

## Send Messages

Type a message and press Enter. The server broadcasts
messages to the connected clients.

Type /quit and press Enter to leave the chat.

## Main Java Concepts

- ServerSocket
- Socket
- Input and output streams
- Multithreading
- ConcurrentHashMap
- AtomicInteger
- Exception handling

## Limitations

This is a basic learning project. It does not include
encryption, password-based authentication, or saved chat history.