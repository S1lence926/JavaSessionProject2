package library.service;

import library.exception.LibraryException;
import library.model.Member;
import library.repository.MemberRepository;

import java.util.List;

public class MemberService {

    private final MemberRepository memberRepo;

    public MemberService(MemberRepository memberRepo) {
        this.memberRepo = memberRepo;
    }

    public Member create(String name, String email, String phone) {
        if (name == null || name.isBlank())
            throw new LibraryException("Имя читателя не может быть пустым.");
        Member member = new Member(name.trim(),
            email != null ? email.trim() : null,
            phone != null ? phone.trim() : null);
        return memberRepo.save(member);
    }

    public Member getById(int id) {
        return memberRepo.findById(id)
            .orElseThrow(() -> new LibraryException("Читатель id=" + id + " не найден."));
    }

    public List<Member> getAll() {
        return memberRepo.findAll();
    }

    public List<Member> search(String name) {
        return memberRepo.findByName(name);
    }

    public void update(int id, String name, String email, String phone) {
        Member m = getById(id);
        if (name  != null && !name.isBlank())  m.setName(name.trim());
        if (email != null)                      m.setEmail(email.trim());
        if (phone != null)                      m.setPhone(phone.trim());
        memberRepo.update(m);
    }

    public void delete(int id) {
        getById(id);
        memberRepo.deleteById(id);
    }

    public long count() {
        return memberRepo.count();
    }
}
