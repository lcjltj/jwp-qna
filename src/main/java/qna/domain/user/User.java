package qna.domain.user;

import org.springframework.util.StringUtils;
import qna.UnAuthorizedException;
import qna.domain.BaseTimeEntity;
import qna.domain.vo.Name;
import qna.domain.vo.Password;
import qna.domain.vo.UserId;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_user_id", columnNames = {"userId"})
})
public class User extends BaseTimeEntity {
    public static final GuestUser GUEST_USER = new GuestUser();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private UserId userId;
    @Embedded
    private Password password;
    @Embedded
    private Name name;
    @Column(length = 50)
    private String email;

    protected User() {
    }

    public User(String userId, String password, String name, String email) {
        this(null, userId, password, name, email);
    }

    public User(Long id, String userId, String password, String name, String email) {
        this.id = id;
        this.userId = UserId.of(userId);
        this.password = Password.of(password);
        this.name = Name.of(name);
        this.email = email;
    }

    public void update(User target) {
        if (!matchUserId(target.userId)) {
            throw new UnAuthorizedException(String.format("유저아이디가 다릅니다. this.userId :: %s, target.userId :: %s", this.userId, target.userId));
        }

        if (!matchPassword(target.password)) {
            throw new UnAuthorizedException("비밀번호가 일치 하지 않습니다.");
        }

        this.name = target.name;
        this.email = target.email;
    }

    private boolean matchUserId(UserId userId) {
        return this.userId.equals(userId);
    }

    private boolean matchPassword(Password targetPassword) {
        return this.password.equals(targetPassword);
    }

    public boolean matchEmail(String email) {
        return this.email.equals(email);
    }

    public boolean matchName(String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        return this.name.equals(Name.of(name));
    }

    public boolean equalsNameAndEmail(User target) {
        if (Objects.isNull(target)) {
            return false;
        }

        return name.equals(target.name) &&
                email.equals(target.email);
    }

    public boolean isGuestUser() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId.getUserId();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    private static class GuestUser extends User {
        @Override
        public boolean isGuestUser() {
            return true;
        }
    }
}