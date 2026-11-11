/**
 * Generic Library Item class
 * @param <T> Type of additional information
 */
public class LibraryItem<T> {

    private String itemID;
    private String title;
    private String author;
    private T extraInfo;

    public LibraryItem(String itemID, String title, String author, T extraInfo) {
        this.itemID = itemID;
        this.title = title;
        this.author = author;
        this.extraInfo = extraInfo;
    }

    public String getItemID() {
        return itemID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public T getExtraInfo() {
        return extraInfo;
    }

    @Override
    public String toString() {
        return "ID: " + itemID +
                "\nTitle: " + title +
                "\nAuthor: " + author +
                "\nExtra Info: " + extraInfo;
    }
}