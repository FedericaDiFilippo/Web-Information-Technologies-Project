# Web Technologies Project: Image Gallery Management

This project implements a web application designed to manage an image gallery. It supports user registration and login, image and album management, and user interaction through comments. The application ensures an intuitive user experience and adheres to best practices in web application development.

## Features

- **User Authentication:** Includes user registration and login functionalities with client- and server-side validation.
- **Image Management:** Users can upload images, associate metadata, and organize them into albums.
- **Albums and Comments:** Users can create albums, add images, and leave comments on images. Albums and comments are linked to their respective users.
- **Dynamic UI:** Implements a responsive user interface with features like image previews, modal views, and AJAX-based asynchronous updates.
- **Custom Sorting:** Allows users to reorder albums and save custom preferences to the server.
- **Error Handling:** Provides user-friendly alerts and error pages for invalid inputs or server issues.

## How It Works

1. **Registration and Login:** Users register by providing a valid email and password. Post-registration, they can log in to access their personalized gallery.
2. **Home Page:** Displays user-created albums and albums shared by others, sorted by creation date in descending order.
3. **Album Page:** Displays thumbnails of images in an album with options to view details, navigate between pages, and add comments.
4. **Dynamic Updates:** All interactions, such as adding comments or navigating images, are handled asynchronously without reloading the page.
5. **Custom Album Order:** Users can drag and drop album titles to reorder them, saving the configuration for future sessions.

## Database Schema

The application employs a relational database with the following schema:

- **`user`**: Stores user details such as username, email, password, and album order.
- **`album`**: Contains album metadata, including title, creator, and creation date.
- **`image`**: Stores image details like title, description, file path, and associated album.
- **`comment`**: Holds user comments on images.

## Technologies Used

- **Backend:** Java-based servlets for server-side logic and database interactions.
- **Frontend:** HTML, CSS, and JavaScript for the user interface.
- **Database:** MySQL for data persistence.
- **AJAX:** Ensures asynchronous communication between the client and server.

## Setup and Deployment

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```
2. Set up the database using the provided schema.
3. Deploy the web application on a servlet container (e.g., Apache Tomcat).
4. Access the application via a web browser.

## Academic Recognition
This project was developed as part of the "Web Technologies" course at Politecnico di Milano and received the highest evaluation.

## Author
Federica Di Filippo  
Politecnico di Milano
