package model;

public class Product {
    private Long id;
    private String title;
    private double price;
    private Status status;
    private Category category;

    public Product() {
    }

    public Product(String title, double price, Status status) {
        this.title = title;
        this.price = price;
        this.status = status;
    }

    public Product(String title, double price, Status status, Category category) {
        this.title = title;
        this.price = price;
        this.status = status;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", category=" + category +
                '}';
    }
}
