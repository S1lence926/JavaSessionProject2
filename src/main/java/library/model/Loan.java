package library.model;

/**
 * Сущность «Выдача книги».
 * Связывает Book и Member (Many-to-Many через эту таблицу).
 */
public class Loan extends BaseEntity {

    private int    bookId;
    private String bookTitle;
    private int    memberId;
    private String memberName;
    private String loanDate;
    private String returnDate;   // null — книга ещё не возвращена

    public Loan() {}

    public Loan(int id, int bookId, String bookTitle,
                int memberId, String memberName,
                String loanDate, String returnDate) {
        super(id);
        this.bookId     = bookId;
        this.bookTitle  = bookTitle;
        this.memberId   = memberId;
        this.memberName = memberName;
        this.loanDate   = loanDate;
        this.returnDate = returnDate;
    }

    public Loan(int bookId, int memberId, String loanDate) {
        this.bookId   = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
    }

    public int    getBookId()      { return bookId; }
    public void   setBookId(int v) { this.bookId = v; }

    public String getBookTitle()          { return bookTitle; }
    public void   setBookTitle(String v)  { this.bookTitle = v; }

    public int    getMemberId()      { return memberId; }
    public void   setMemberId(int v) { this.memberId = v; }

    public String getMemberName()          { return memberName; }
    public void   setMemberName(String v)  { this.memberName = v; }

    public String getLoanDate()          { return loanDate; }
    public void   setLoanDate(String v)  { this.loanDate = v; }

    public String getReturnDate()          { return returnDate; }
    public void   setReturnDate(String v)  { this.returnDate = v; }

    public boolean isReturned() {
        return returnDate != null && !returnDate.isBlank();
    }

    @Override
    public String toString() {
        return String.format(
            "[%d] Книга: %-30s | Читатель: %-20s | Взята: %s | Возвращена: %s",
            id,
            bookTitle  != null ? bookTitle  : "#" + bookId,
            memberName != null ? memberName : "#" + memberId,
            loanDate,
            isReturned() ? returnDate : "ещё нет"
        );
    }
}
