package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.repository.BookRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_ISBN = "AAAAAAAAAA";
    private static final String UPDATED_ISBN = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BOOKDATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BOOKDATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DISTRIBUTOR = "AAAAAAAAAA";
    private static final String UPDATED_DISTRIBUTOR = "BBBBBBBBBB";

    private static final byte[] DEFAULT_BOOK_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BOOK_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BOOK_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BOOK_IMAGE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_BOOK_PDF = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BOOK_PDF = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BOOK_PDF_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BOOK_PDF_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_PRODUCER = "AAAAAAAAAA";
    private static final String UPDATED_PRODUCER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookRepository bookRepository;

    @Mock
    private BookRepository bookRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookMockMvc;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .title(DEFAULT_TITLE)
            .isbn(DEFAULT_ISBN)
            .bookdate(DEFAULT_BOOKDATE)
            .distributor(DEFAULT_DISTRIBUTOR)
            .bookImage(DEFAULT_BOOK_IMAGE)
            .bookImageContentType(DEFAULT_BOOK_IMAGE_CONTENT_TYPE)
            .bookPdf(DEFAULT_BOOK_PDF)
            .bookPdfContentType(DEFAULT_BOOK_PDF_CONTENT_TYPE)
            .producer(DEFAULT_PRODUCER);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .bookdate(UPDATED_BOOKDATE)
            .distributor(UPDATED_DISTRIBUTOR)
            .bookImage(UPDATED_BOOK_IMAGE)
            .bookImageContentType(UPDATED_BOOK_IMAGE_CONTENT_TYPE)
            .bookPdf(UPDATED_BOOK_PDF)
            .bookPdfContentType(UPDATED_BOOK_PDF_CONTENT_TYPE)
            .producer(UPDATED_PRODUCER);
        return book;
    }

    @BeforeEach
    public void initTest() {
        book = createEntity(em);
    }

    @Test
    @Transactional
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().size();
        // Create the Book
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isCreated());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getIsbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(testBook.getBookdate()).isEqualTo(DEFAULT_BOOKDATE);
        assertThat(testBook.getDistributor()).isEqualTo(DEFAULT_DISTRIBUTOR);
        assertThat(testBook.getBookImage()).isEqualTo(DEFAULT_BOOK_IMAGE);
        assertThat(testBook.getBookImageContentType()).isEqualTo(DEFAULT_BOOK_IMAGE_CONTENT_TYPE);
        assertThat(testBook.getBookPdf()).isEqualTo(DEFAULT_BOOK_PDF);
        assertThat(testBook.getBookPdfContentType()).isEqualTo(DEFAULT_BOOK_PDF_CONTENT_TYPE);
        assertThat(testBook.getProducer()).isEqualTo(DEFAULT_PRODUCER);
    }

    @Test
    @Transactional
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);

        int databaseSizeBeforeCreate = bookRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setTitle(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBookdateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setBookdate(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDistributorIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setDistributor(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkProducerIsRequired() throws Exception {
        int databaseSizeBeforeTest = bookRepository.findAll().size();
        // set the field null
        book.setProducer(null);

        // Create the Book, which fails.

        restBookMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isBadRequest());

        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBooks() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get all the bookList
        restBookMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(book.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].isbn").value(hasItem(DEFAULT_ISBN)))
            .andExpect(jsonPath("$.[*].bookdate").value(hasItem(DEFAULT_BOOKDATE.toString())))
            .andExpect(jsonPath("$.[*].distributor").value(hasItem(DEFAULT_DISTRIBUTOR)))
            .andExpect(jsonPath("$.[*].bookImageContentType").value(hasItem(DEFAULT_BOOK_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].bookImage").value(hasItem(Base64Utils.encodeToString(DEFAULT_BOOK_IMAGE))))
            .andExpect(jsonPath("$.[*].bookPdfContentType").value(hasItem(DEFAULT_BOOK_PDF_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].bookPdf").value(hasItem(Base64Utils.encodeToString(DEFAULT_BOOK_PDF))))
            .andExpect(jsonPath("$.[*].producer").value(hasItem(DEFAULT_PRODUCER)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsEnabled() throws Exception {
        when(bookRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBooksWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bookRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBookMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bookRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        // Get the book
        restBookMockMvc
            .perform(get(ENTITY_API_URL_ID, book.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(book.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.isbn").value(DEFAULT_ISBN))
            .andExpect(jsonPath("$.bookdate").value(DEFAULT_BOOKDATE.toString()))
            .andExpect(jsonPath("$.distributor").value(DEFAULT_DISTRIBUTOR))
            .andExpect(jsonPath("$.bookImageContentType").value(DEFAULT_BOOK_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.bookImage").value(Base64Utils.encodeToString(DEFAULT_BOOK_IMAGE)))
            .andExpect(jsonPath("$.bookPdfContentType").value(DEFAULT_BOOK_PDF_CONTENT_TYPE))
            .andExpect(jsonPath("$.bookPdf").value(Base64Utils.encodeToString(DEFAULT_BOOK_PDF)))
            .andExpect(jsonPath("$.producer").value(DEFAULT_PRODUCER));
    }

    @Test
    @Transactional
    void getNonExistingBook() throws Exception {
        // Get the book
        restBookMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).get();
        // Disconnect from session so that the updates on updatedBook are not directly saved in db
        em.detach(updatedBook);
        updatedBook
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .bookdate(UPDATED_BOOKDATE)
            .distributor(UPDATED_DISTRIBUTOR)
            .bookImage(UPDATED_BOOK_IMAGE)
            .bookImageContentType(UPDATED_BOOK_IMAGE_CONTENT_TYPE)
            .bookPdf(UPDATED_BOOK_PDF)
            .bookPdfContentType(UPDATED_BOOK_PDF_CONTENT_TYPE)
            .producer(UPDATED_PRODUCER);

        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBook.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getIsbn()).isEqualTo(UPDATED_ISBN);
        assertThat(testBook.getBookdate()).isEqualTo(UPDATED_BOOKDATE);
        assertThat(testBook.getDistributor()).isEqualTo(UPDATED_DISTRIBUTOR);
        assertThat(testBook.getBookImage()).isEqualTo(UPDATED_BOOK_IMAGE);
        assertThat(testBook.getBookImageContentType()).isEqualTo(UPDATED_BOOK_IMAGE_CONTENT_TYPE);
        assertThat(testBook.getBookPdf()).isEqualTo(UPDATED_BOOK_PDF);
        assertThat(testBook.getBookPdfContentType()).isEqualTo(UPDATED_BOOK_PDF_CONTENT_TYPE);
        assertThat(testBook.getProducer()).isEqualTo(UPDATED_PRODUCER);
    }

    @Test
    @Transactional
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, book.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook.bookImage(UPDATED_BOOK_IMAGE).bookImageContentType(UPDATED_BOOK_IMAGE_CONTENT_TYPE);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getIsbn()).isEqualTo(DEFAULT_ISBN);
        assertThat(testBook.getBookdate()).isEqualTo(DEFAULT_BOOKDATE);
        assertThat(testBook.getDistributor()).isEqualTo(DEFAULT_DISTRIBUTOR);
        assertThat(testBook.getBookImage()).isEqualTo(UPDATED_BOOK_IMAGE);
        assertThat(testBook.getBookImageContentType()).isEqualTo(UPDATED_BOOK_IMAGE_CONTENT_TYPE);
        assertThat(testBook.getBookPdf()).isEqualTo(DEFAULT_BOOK_PDF);
        assertThat(testBook.getBookPdfContentType()).isEqualTo(DEFAULT_BOOK_PDF_CONTENT_TYPE);
        assertThat(testBook.getProducer()).isEqualTo(DEFAULT_PRODUCER);
    }

    @Test
    @Transactional
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeUpdate = bookRepository.findAll().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .title(UPDATED_TITLE)
            .isbn(UPDATED_ISBN)
            .bookdate(UPDATED_BOOKDATE)
            .distributor(UPDATED_DISTRIBUTOR)
            .bookImage(UPDATED_BOOK_IMAGE)
            .bookImageContentType(UPDATED_BOOK_IMAGE_CONTENT_TYPE)
            .bookPdf(UPDATED_BOOK_PDF)
            .bookPdfContentType(UPDATED_BOOK_PDF_CONTENT_TYPE)
            .producer(UPDATED_PRODUCER);

        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBook.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            )
            .andExpect(status().isOk());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getIsbn()).isEqualTo(UPDATED_ISBN);
        assertThat(testBook.getBookdate()).isEqualTo(UPDATED_BOOKDATE);
        assertThat(testBook.getDistributor()).isEqualTo(UPDATED_DISTRIBUTOR);
        assertThat(testBook.getBookImage()).isEqualTo(UPDATED_BOOK_IMAGE);
        assertThat(testBook.getBookImageContentType()).isEqualTo(UPDATED_BOOK_IMAGE_CONTENT_TYPE);
        assertThat(testBook.getBookPdf()).isEqualTo(UPDATED_BOOK_PDF);
        assertThat(testBook.getBookPdfContentType()).isEqualTo(UPDATED_BOOK_PDF_CONTENT_TYPE);
        assertThat(testBook.getProducer()).isEqualTo(UPDATED_PRODUCER);
    }

    @Test
    @Transactional
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, book.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(book))
            )
            .andExpect(status().isBadRequest());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().size();
        book.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(book)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBook() throws Exception {
        // Initialize the database
        bookRepository.saveAndFlush(book);

        int databaseSizeBeforeDelete = bookRepository.findAll().size();

        // Delete the book
        restBookMockMvc
            .perform(delete(ENTITY_API_URL_ID, book.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
