package com.codepath.android.booksearch.models;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {
  private String openLibraryId;
  private String author;
  private String title;

  private int numberOfPgaes;

  private String publisher;

  public int getNumberOfPgaes() {
    return numberOfPgaes;
  }

  public String getPublisher() {
    return publisher;
  }


  public String getOpenLibraryId() {
    return openLibraryId;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  // Get book cover from covers API
  public String getCoverUrl() {
    return "http://covers.openlibrary.org/b/olid/" + openLibraryId + "-L.jpg?default=false";
  }

  // Returns a Book given the expected JSON
  public static Book fromJson(JSONObject jsonObject) {
    Book book = new Book();
    try {
      // Deserialize json into object fields
      // Check if a cover edition is available
      if (jsonObject.has("cover_edition_key")) {
        book.openLibraryId = jsonObject.getString("cover_edition_key");
      } else if (jsonObject.has("edition_key")) {
        final JSONArray ids = jsonObject.getJSONArray("edition_key");
        book.openLibraryId = ids.getString(0);
      }
      book.title = jsonObject.has("title_suggest") ? jsonObject.getString("title_suggest") : "";

      if (jsonObject.has("publisher")) {
        book.publisher = jsonObject.getString("publisher");
      } else {
        book.publisher = "unknown";
      }

      if (jsonObject.has("number_of_pages")) {
        book.numberOfPgaes = jsonObject.getInt("number_of_pages");
      } else {
        book.numberOfPgaes = 5;
      }

      book.author = getAuthor(jsonObject);
    } catch (JSONException e) {
      e.printStackTrace();
      return null;
    }
    // Return new object
    return book;
  }

  // Return comma separated author list when there is more than one author
  private static String getAuthor(final JSONObject jsonObject) {
    try {
      final JSONArray authors = jsonObject.getJSONArray("author_name");
      int numAuthors = authors.length();
      final String[] authorStrings = new String[numAuthors];
      for (int i = 0; i < numAuthors; ++i) {
        authorStrings[i] = authors.getString(i);
      }
      return TextUtils.join(", ", authorStrings);
    } catch (JSONException e) {
      return "";
    }
  }

  // Decodes array of book json results into business model objects
  public static ArrayList<Book> fromJson(JSONArray jsonArray) {
    ArrayList<Book> books = new ArrayList<Book>(jsonArray.length());
    // Process each result in json array, decode and convert to business
    // object
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject bookJson = null;
      try {
        bookJson = jsonArray.getJSONObject(i);
      } catch (Exception e) {
        e.printStackTrace();
        continue;
      }
      Book book = Book.fromJson(bookJson);
      if (book != null) {
        books.add(book);
      }
    }
    return books;
  }
}
