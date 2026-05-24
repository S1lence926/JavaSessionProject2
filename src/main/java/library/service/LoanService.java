package library.service;

import library.exception.LibraryException;
import library.model.Book;
import library.model.Loan;
import library.model.Member;
import library.repository.BookRepository;
import library.repository.LoanRepository;
import library.repository.MemberRepository;

import java.time.LocalDate;
import java.util.List;

public class LoanService {

    private final LoanRepository   loanRepo;
    private final BookRepository   bookRepo;
    private final MemberRepository memberRepo;

    public LoanService(LoanRepository loanRepo,
                       BookRepository bookRepo,
                       MemberRepository memberRepo) {
        this.loanRepo   = loanRepo;
        this.bookRepo   = bookRepo;
        this.memberRepo = memberRepo;
    }

    /**
     * Выдать книгу читателю. Уменьшает счётчик copies.
     */
    public Loan issueBook(int bookId, int memberId) {
        Book book = bookRepo.findById(bookId)
            .orElseThrow(() -> new LibraryException("Книга id=" + bookId + " не найдена."));
        memberRepo.findById(memberId)
            .orElseThrow(() -> new LibraryException("Читатель id=" + memberId + " не найден."));

        if (book.getCopies() <= 0)
            throw new LibraryException("Нет доступных экземпляров книги \"" + book.getTitle() + "\".");

        bookRepo.decrementCopies(bookId);

        Loan loan = new Loan(bookId, memberId, LocalDate.now().toString());
        return loanRepo.save(loan);
    }

    /**
     * Вернуть книгу. Увеличивает счётчик copies.
     */
    public void returnBook(int loanId) {
        Loan loan = loanRepo.findById(loanId)
            .orElseThrow(() -> new LibraryException("Запись о выдаче id=" + loanId + " не найдена."));
        if (loan.isReturned())
            throw new LibraryException("Книга по этой записи уже была возвращена.");

        loan.setReturnDate(LocalDate.now().toString());
        loanRepo.update(loan);
        bookRepo.incrementCopies(loan.getBookId());
    }

    public Loan getById(int id) {
        return loanRepo.findById(id)
            .orElseThrow(() -> new LibraryException("Запись о выдаче id=" + id + " не найдена."));
    }

    public List<Loan> getAll() {
        return loanRepo.findAll();
    }

    public List<Loan> getActive() {
        return loanRepo.findActive();
    }

    public List<Loan> getByMember(int memberId) {
        return loanRepo.findByMemberId(memberId);
    }

    public void delete(int id) {
        loanRepo.deleteById(id);
    }

    public long count() {
        return loanRepo.count();
    }
}
