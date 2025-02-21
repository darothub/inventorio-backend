package org.darot.authserviceapplication.core.model

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
data class AuthUser (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true)
    private val email: String,
    @Column(nullable = false)
    private var password: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(role.name))
    }

    override fun getPassword(): String? = password

    override fun getUsername(): String = email

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}
@Entity
data class Student(
    @Id
    val id: Long? = null,
    val email: String,
    @ManyToOne
    @JoinColumn(name = "class_room_id")
    var classRoom: ClassRoom
)
@Entity
data class ClassRoom(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var classRoomId: Long? = null,
    @OneToMany(mappedBy = "class_room", cascade = [CascadeType.ALL], orphanRemoval = true, targetEntity = Student::class)
    private var students: List<Student> = listOf(),
)