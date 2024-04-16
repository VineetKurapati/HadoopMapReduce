# Running Hadoop and MapReduce with Docker

## Setup Docker Hadoop Client

1. Open your terminal and navigate to the directory containing your Docker Hadoop setup.

2. Start the Docker containers using the following command:
    ```
    docker-compose up -d
    ```

## Performing the Operations

### 1. Compilation of Java Code (Optional)

If you have made changes to the Java code and need to compile it, follow these steps:

   i. Compile the Java code using the following command, ensuring to replace `<HADOOP_LOCAL_FOLDER_DESTINATION>` with the appropriate path:
      ```
      javac -classpath <HADOOP_LOCAL_FOLDER_DESTINATION>/share/hadoop/common/hadoop-common-3.4.0.jar:<HADOOP_LOCAL_FOLDER_DESTINATION>/share/hadoop/mapreduce/hadoop-mapreduce-client-core-3.4.0.jar <JAVA_FILE_NAME>
      ```

   ii. Create a JAR file using the compiled class files:
      ```
      jar cvf <Name_of_JAR_FILE> <ClassName>*.class
      ```

### 2. Copying Code and Input File to Docker Container

Copy the Java code and the input file to the Docker container:

   i. Use the `docker cp` command to copy the files:
      ```
      docker cp <Name_of_the_file> namenode:/
      ```

### 3. Accessing the Docker Container

Access the Docker container using the following command:
   ```
   docker exec -it namenode /bin/bash
   ```

### 4. Creating a Hadoop Directory for Input File

Create a directory in Hadoop for the input file:

   ```
   hdfs dfs -mkdir -p /user/root/input
   ```

### 5. Putting the Input File in HDFS

Put the input file into the Hadoop Distributed File System (HDFS):

   ```
   hdfs dfs -put <file_name> <input_directory_path>
   ```

### 6. Running the Hadoop MapReduce Job (For Q1, Q2, Q3)

Finally, execute the Hadoop MapReduce job using the following command:

   ```
   hadoop jar <jar_file_name> <class_name> <input_path> <output_path>
   ```
Replace placeholders such as `<file_name>`, `<input_path>`, `<output_path>`, `<class_name>`, `<Name_of_JAR_FILE>`, and `<JAVA_FILE_NAME>` with the appropriate values as per your setup.
### 7. Running the Hadoop MapReduce Job (For Q4)

hadoop jar <jar_file_name> <class_name> <input_path> <output_path> <num clusters>