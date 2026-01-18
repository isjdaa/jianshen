// MemberService class
public class MemberService {
    private MemberDAO memberDAO;

    public void registerMember(Member member) {
        memberDAO.addMember(member);
    }
    // Other service methods...
}
