import kotlin.concurrent.withLock
import java.util.concurrent.locks.ReentrantLock
/**
 * Bank implementation.
 *
 * This implementation has to be made thread-safe.
 *
 * @author Селиванов Николай
 */
class BankImpl(n: Int) : Bank {
    private val accounts: Array<Account> = Array(n) { Account() }

    override val numberOfAccounts: Int
        get() = accounts.size

    /**
     * This method has to be made thread-safe.
     */
    override fun getAmount(index: Int): Long {
        val account = accounts[index]
        return account.lock.withLock { account.amount }
    }

    /**
     * This method has to be made thread-safe.
     */
    override val totalAmount: Long
        get() = accounts.map(Account::lock).let { locks ->
            locks.forEach(ReentrantLock::lock)
            return accounts.sumOf(Account::amount).also { locks.forEach(ReentrantLock::unlock) }
        }

    /**
     * This method has to be made thread-safe.
     */
    override fun deposit(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        account.lock.withLock {
            check(!(amount > Bank.MAX_AMOUNT || account.amount + amount > Bank.MAX_AMOUNT)) { "Overflow" }
            account.amount += amount
            return account.amount
        }
    }

    /**
     * This method has to be made thread-safe.
     */
    override fun withdraw(index: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[index]
        account.lock.withLock {
            check(account.amount - amount >= 0) { "Underflow" }
            account.amount -= amount
            return account.amount
        }
    }

    /**
     * This method has to be made thread-safe.
     */
    override fun transfer(fromIndex: Int, toIndex: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromIndex != toIndex) { "fromIndex == toIndex" }
        accounts[minOf(fromIndex, toIndex)].lock.withLock {
            accounts[maxOf(fromIndex, toIndex)].lock.withLock {
                val from = accounts[fromIndex]
                val to = accounts[toIndex]
                check(amount <= from.amount) { "Underflow" }
                check(!(amount > Bank.MAX_AMOUNT || to.amount + amount > Bank.MAX_AMOUNT)) { "Overflow" }
                from.amount -= amount
                to.amount += amount
            }
        }
    }

    /**
     * Private account data structure.
     */
    class Account {
        /**
         * Amount of funds in this account.
         */
        var amount: Long = 0

        val lock: ReentrantLock = ReentrantLock()
    }
}