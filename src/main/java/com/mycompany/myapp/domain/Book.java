package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Book.
 */
@Entity
@Table(name = "book")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "description")
    private String description;

    @Column(name = "content")
    private String content;

    @Column(name = "links")
    private String links;

    @NotNull
    @Column(name = "bookdate", nullable = false)
    private LocalDate bookdate;

    @NotNull
    @Column(name = "distributor", nullable = false)
    private String distributor;

    @Lob
    @Column(name = "book_image")
    private byte[] bookImage;

    @Column(name = "book_image_content_type")
    private String bookImageContentType;

    @Lob
    @Column(name = "book_pdf")
    private byte[] bookPdf;

    @Column(name = "book_pdf_content_type")
    private String bookPdfContentType;

    @NotNull
    @Column(name = "producer", nullable = false)
    private String producer;

    @ManyToMany
    @JoinTable(name = "rel_book__author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @JsonIgnoreProperties(value = { "books" }, allowSetters = true)
    private Set<Author> authors = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book id(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public Book isbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return this.description;
    }

    public Book description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return this.content;
    }

    public Book content(String content) {
        this.content = content;
        return this;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinks() {
        return this.links;
    }

    public Book links(String links) {
        this.links = links;
        return this;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public LocalDate getBookdate() {
        return this.bookdate;
    }

    public Book bookdate(LocalDate bookdate) {
        this.bookdate = bookdate;
        return this;
    }

    public void setBookdate(LocalDate bookdate) {
        this.bookdate = bookdate;
    }

    public String getDistributor() {
        return this.distributor;
    }

    public Book distributor(String distributor) {
        this.distributor = distributor;
        return this;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public byte[] getBookImage() {
        return this.bookImage;
    }

    public Book bookImage(byte[] bookImage) {
        this.bookImage = bookImage;
        return this;
    }

    public void setBookImage(byte[] bookImage) {
        this.bookImage = bookImage;
    }

    public String getBookImageContentType() {
        return this.bookImageContentType;
    }

    public Book bookImageContentType(String bookImageContentType) {
        this.bookImageContentType = bookImageContentType;
        return this;
    }

    public void setBookImageContentType(String bookImageContentType) {
        this.bookImageContentType = bookImageContentType;
    }

    public byte[] getBookPdf() {
        return this.bookPdf;
    }

    public Book bookPdf(byte[] bookPdf) {
        this.bookPdf = bookPdf;
        return this;
    }

    public void setBookPdf(byte[] bookPdf) {
        this.bookPdf = bookPdf;
    }

    public String getBookPdfContentType() {
        return this.bookPdfContentType;
    }

    public Book bookPdfContentType(String bookPdfContentType) {
        this.bookPdfContentType = bookPdfContentType;
        return this;
    }

    public void setBookPdfContentType(String bookPdfContentType) {
        this.bookPdfContentType = bookPdfContentType;
    }

    public String getProducer() {
        return this.producer;
    }

    public Book producer(String producer) {
        this.producer = producer;
        return this;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Set<Author> getAuthors() {
        return this.authors;
    }

    public Book authors(Set<Author> authors) {
        this.setAuthors(authors);
        return this;
    }

    public Book addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
        return this;
    }

    public Book removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
        return this;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", isbn='" + getIsbn() + "'" +
            ", description='" + getDescription() + "'" +
            ", content='" + getContent() + "'" +
            ", links='" + getLinks() + "'" +
            ", bookdate='" + getBookdate() + "'" +
            ", distributor='" + getDistributor() + "'" +
            ", bookImage='" + getBookImage() + "'" +
            ", bookImageContentType='" + getBookImageContentType() + "'" +
            ", bookPdf='" + getBookPdf() + "'" +
            ", bookPdfContentType='" + getBookPdfContentType() + "'" +
            ", producer='" + getProducer() + "'" +
            "}";
    }
}
