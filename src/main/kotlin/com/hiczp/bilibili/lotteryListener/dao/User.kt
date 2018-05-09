package com.hiczp.bilibili.lotteryListener.dao

//@Entity
//data class User(
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        var id: Long,
//
//        /**
//         * 用户名就是邮箱
//         */
//        @Column(nullable = false, length = 64, unique = true)
//        var username: String,
//
//        @Column(nullable = false, length = 64)
//        var password: String,
//
//        @OneToMany(cascade = [CascadeType.REMOVE])
//        @JoinColumn(name = "user")
//        var hooks:List<Hook>
//)
//
//@Repository
//interface UserRepository : CrudRepository<User, Long> {
//    fun findByUsername(username: String): User
//
//    fun findByUsernameAndPassword(username: String, password: String): User
//}
