# Student Evaluation Rubric - Library Service Unit Testing

**Total Score: 20 Points**

This evaluation assesses students' ability to implement unit tests using Test-Driven Development (TDD) principles, proper exception handling, mocking, and testing best practices.

## Evaluation Overview

### Student TODOs Distribution
- **TODO 1**: LoanService Exception Test (4 points)
- **TODO 2**: BookService TDD Implementation (16 points)

---

## TODO 1: LoanServiceTest Exception Handling (4 Points)

**Location**: `src/test/java/com/tuto/library/service/LoanServiceTest.java:43`
**Test Method**: `shouldThrowInvalidLoanOperationException_whenReturningNonActiveLoan()`

### Scoring Criteria

| Criteria | Full Points | Partial Points | Score Range |
|----------|-------------|----------------|-------------|
| **Test Setup (1 point)** | Mock configuration and test data creation | Basic setup with minor issues | 0-1 |
| **Exception Testing (2 points)** | Proper exception verification using any valid approach | Incorrect exception handling | 0-2 |
| **Business Logic Validation (1 point)** | Tests with non-active loan status (RETURNED/OVERDUE) | Basic test without status validation | 0-1 |

### Detailed Scoring

**Full Score (4/4):**
- ✅ Properly mocks `LoanRepository.findById()` to return loan with `RETURNED` or `OVERDUE` status
- ✅ Uses any valid exception testing approach:
  - `assertThrows(InvalidLoanOperationException.class, () -> ...)` (preferred pattern from existing tests)
  - `assertThatThrownBy(() -> ...).isInstanceOf(InvalidLoanOperationException.class)`
  - `@Test(expected = InvalidLoanOperationException.class)` (JUnit 4 style)
  - Try-catch with `fail()` if no exception thrown
- ✅ Verifies correct exception message (when applicable)
- ✅ Clean test structure with Given-When-Then pattern

**Partial Score Examples:**
- **3/4**: Correct exception testing but missing proper status setup
- **2/4**: Basic exception test but incorrect exception type or poor mock setup
- **1/4**: Attempt at test implementation but major flaws in logic
- **0/4**: Empty test or completely incorrect approach

---

## TODO 2: BookService TDD Implementation (16 Points)

**Location**: `src/test/java/com/tuto/library/service/BookServiceTest.java`

This TODO requires implementing 5 test methods using TDD approach, plus implementing the corresponding service methods.

### Test Methods Breakdown (3.2 points each)

#### 2.1 `shouldReturnTrue_whenBookIsAvailable()` (3.2 points)
- **Test Logic (1.6 pts)**: Proper setup with available book and correct assertion
- **Service Implementation (1.6 pts)**: Implementing `isBookAvailable()` method in BookService

#### 2.2 `shouldDecreaseAvailableCopies_whenBorrowingBook()` (3.2 points)
- **Test Logic (1.6 pts)**: Test verifies available copies decrease after borrowing
- **Service Implementation (1.6 pts)**: Implementing `borrowBook()` method with inventory management

#### 2.3 `shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies()` (3.2 points)
- **Test Logic (1.6 pts)**: Exception test for zero available copies
- **Service Implementation (1.6 pts)**: Exception throwing logic in service method

#### 2.4 `shouldIncreaseAvailableCopies_whenReturningBook()` (3.2 points)
- **Test Logic (1.6 pts)**: Test verifies available copies increase after return
- **Service Implementation (1.6 pts)**: Implementing `returnBook()` method

#### 2.5 `shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies()` (3.2 points)
- **Test Logic (1.6 pts)**: Edge case test for returning when all copies available
- **Service Implementation (1.6 pts)**: Business rule validation in service

### TDD Process Evaluation (Additional Considerations)

**Bonus Considerations** (can offset minor deductions):
- **Red-Green-Refactor**: Evidence of TDD cycle (failing tests first)
- **Test Quality**: Clear test names, proper assertions, good coverage
- **Code Quality**: Clean implementation, proper error handling
- **Integration**: Tests work with existing LoanService integration

### Detailed Scoring Guidelines

**Per Test Method (3.2 points each):**

**Excellent (3.0-3.2):**
- ✅ Complete test implementation with proper Given-When-Then structure
- ✅ Correct mocking and assertions
- ✅ Full service method implementation
- ✅ Proper exception handling where applicable

**Good (2.4-2.9):**
- ✅ Test implemented with minor issues
- ✅ Service method mostly correct
- ⚠️ Small gaps in logic or assertions

**Satisfactory (1.6-2.3):**
- ✅ Basic test structure present
- ✅ Partial service implementation
- ⚠️ Missing key logic or incorrect assertions

**Needs Improvement (0.8-1.5):**
- ⚠️ Test exists but significant flaws
- ⚠️ Minimal service implementation
- ❌ Major logical errors

**Unsatisfactory (0-0.7):**
- ❌ Empty test or no implementation
- ❌ Completely incorrect approach

---

## Overall Evaluation Criteria

### Code Quality Standards
- **Testing Best Practices**: Use of proper assertion libraries (AssertJ), clear test structure
- **Mocking**: Appropriate use of Mockito for repository layer
- **Exception Handling**: Proper testing of business rule violations
- **Code Coverage**: Tests cover both happy path and error scenarios

### Common Deductions
- **-0.5**: Poor test naming or structure
- **-0.5**: Missing imports or compilation errors
- **-1.0**: Incorrect exception types
- **-1.0**: Missing mock configurations
- **-2.0**: Tests that don't actually test the intended behavior

### Exception Testing Guidelines
**Note for Graders**: Students should receive full marks for exception testing if they use any valid approach that correctly verifies the expected exception is thrown. The codebase already contains an example using `assertThrows()`, but alternative valid patterns should be accepted with full credit.

### Grade Scale
- **18-20**: Excellent - Demonstrates mastery of TDD and testing principles
- **15-17**: Good - Solid understanding with minor gaps
- **12-14**: Satisfactory - Basic implementation with some issues
- **8-11**: Needs Improvement - Significant gaps in understanding
- **0-7**: Unsatisfactory - Major deficiencies or incomplete work

---

## Evaluation Notes

**For Instructors:**
- Review both test implementation AND service method implementation
- Check that tests actually fail before service methods are implemented (TDD)
- Verify integration between tests and existing codebase
- Consider partial credit for incomplete but logical attempts

**For Students:**
- Focus on writing failing tests first (Red phase)
- Implement minimal code to pass tests (Green phase)
- Refactor while keeping tests green (Refactor phase)
- Pay attention to business rules and edge cases