package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends ActionBarActivity {
  private ImageView ivBookCover;
  private TextView tvTitle;
  private TextView tvAuthor;
  private TextView tvPages;
  private TextView tvPublishers;
  private ShareActionProvider miShareAction;
  private Intent shareIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book_detail);
    // Fetch views
    ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
    tvTitle = (TextView) findViewById(R.id.tvTitle);
    tvAuthor = (TextView) findViewById(R.id.tvAuthor);
    tvPages = (TextView) findViewById(R.id.number_of_pages);
    tvPublishers = (TextView) findViewById(R.id.publishers);


    // Extract book object from intent extras
    Intent intent = getIntent();
    final Book book = (Book) intent.getSerializableExtra("book");

    // Use book object to populate data into views
    Picasso.with(getApplicationContext()).load(Uri.parse(book.getCoverUrl())).placeholder(R.drawable.ic_nocover).into(
        ivBookCover, new Callback() {
          @Override
          public void onSuccess() {
            setUpShareIntent(book);
          }

          @Override
          public void onError() {

          }
        });
    tvTitle.setText(book.getTitle());
    tvAuthor.setText(book.getAuthor());
    tvPages.setText(String.valueOf(book.getNumberOfPgaes()) + " Pages");
    tvPublishers.setText("Publishers: " + book.getPublisher().replace("[", "").replace("]", ""));
    ActionBar actionBar = getSupportActionBar(); // or getActionBar();
    getSupportActionBar().setTitle(book.getTitle()); // set the top title
  }


  public void setUpShareIntent(Book book) {
    // Fetch Bitmap Uri locally
    ImageView ivImage = (ImageView) findViewById(R.id.ivBookCover);
    Uri bmpUri = getLocalBitmapUri(ivImage); // see previous remote images section
    // Create share intent as described above
    shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
    shareIntent.putExtra(Intent.EXTRA_TEXT, book.getTitle());
    shareIntent.setType("image/*");
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
  }

  // Returns the URI path to the Bitmap displayed in specified ImageView
  public Uri getLocalBitmapUri(ImageView imageView) {
    // Extract Bitmap from ImageView drawable
    Drawable drawable = imageView.getDrawable();
    Bitmap bmp = null;
    if (drawable instanceof BitmapDrawable) {
      bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
    } else {
      return null;
    }
    // Store image to default external storage directory
    Uri bmpUri = null;
    try {
      // Use methods on Context to access package-specific directories on external storage.
      // This way, you don't need to request external read/write permission.
      // See https://youtu.be/5xVh-7ywKpE?t=25m25s
      File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
      FileOutputStream out = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
      out.close();
      bmpUri = Uri.fromFile(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bmpUri;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_book_detail, menu);
    MenuItem item = menu.findItem(R.id.menu_item_share);
    miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
    miShareAction.setShareIntent(shareIntent);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


}
