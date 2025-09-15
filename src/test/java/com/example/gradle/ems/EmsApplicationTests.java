package com.example.gradle.ems;

import com.example.gradle.ems.EmployeeRepository.EmployeeRepository;
import com.example.gradle.ems.Entity.Employee;
import com.example.gradle.ems.Service.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

	@Mock
	private EmployeeRepository employeeRepository;

	@InjectMocks
	private EmployeeServiceImpl employeeService;

	private Employee employee;

	@BeforeEach
	void setUp() {
		employee = new Employee(1L, "John Doe", "IT", 60000.0);
	}

	@Test
	void createEmployee_validInput_success() {
		// Arrange
		when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

		// Act
		Employee createdEmployee = employeeService.createEmployee(employee);

		// Assert
		assertNotNull(createdEmployee);
		assertEquals(employee.getName(), createdEmployee.getName());
		verify(employeeRepository, times(1)).save(employee);
	}

	@Test
	void createEmployee_nullName_throwsException() {
		// Arrange
		employee.setName(null);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employee));
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void createEmployee_zeroOrNegativeSalary_throwsException() {
		// Arrange
		employee.setSalary(0);

		// Act & Assert
		assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(employee));
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void getEmployeeById_validId_success() {
		// Arrange
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

		// Act
		Employee foundEmployee = employeeService.getEmployeeById(1L);

		// Assert
		assertNotNull(foundEmployee);
		assertEquals(employee.getName(), foundEmployee.getName());
		verify(employeeRepository, times(1)).findById(1L);
	}

	@Test
	void getEmployeeById_invalidId_throwsException() {
		// Arrange
		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NoSuchElementException.class, () -> employeeService.getEmployeeById(99L));
		verify(employeeRepository, times(1)).findById(99L);
	}

	@Test
	void getAllEmployees_success() {
		// Arrange
		List<Employee> employees = Arrays.asList(employee, new Employee(2L, "Jane Smith", "HR", 70000.0));
		when(employeeRepository.findAll()).thenReturn(employees);

		// Act
		List<Employee> allEmployees = employeeService.getAllEmployees();

		// Assert
		assertNotNull(allEmployees);
		assertEquals(2, allEmployees.size());
		verify(employeeRepository, times(1)).findAll();
	}

	@Test
	void updateEmployee_validId_success() {
		// Arrange
		Employee updatedEmployee = new Employee(1L, "Johnny Doe", "IT", 65000.0);
		when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
		when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

		// Act
		Employee result = employeeService.updateEmployee(1L, updatedEmployee);

		// Assert
		assertNotNull(result);
		assertEquals("Johnny Doe", result.getName());
		assertEquals(65000.0, result.getSalary());
		verify(employeeRepository, times(1)).findById(1L);
		verify(employeeRepository, times(1)).save(any(Employee.class));
	}

	@Test
	void updateEmployee_invalidId_throwsException() {
		// Arrange
		Employee updatedEmployee = new Employee(99L, "Non Existent", "IT", 65000.0);
		when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(NoSuchElementException.class, () -> employeeService.updateEmployee(99L, updatedEmployee));
		verify(employeeRepository, times(1)).findById(99L);
		verify(employeeRepository, never()).save(any(Employee.class));
	}

	@Test
	void deleteEmployee_validId_success() {
		// Arrange
		when(employeeRepository.existsById(1L)).thenReturn(true);
		doNothing().when(employeeRepository).deleteById(1L);

		// Act
		assertDoesNotThrow(() -> employeeService.deleteEmployee(1L));

		// Assert
		verify(employeeRepository, times(1)).existsById(1L);
		verify(employeeRepository, times(1)).deleteById(1L);
	}

	@Test
	void deleteEmployee_invalidId_throwsException() {
		// Arrange
		when(employeeRepository.existsById(99L)).thenReturn(false);

		// Act & Assert
		assertThrows(NoSuchElementException.class, () -> employeeService.deleteEmployee(99L));
		verify(employeeRepository, times(1)).existsById(99L);
		verify(employeeRepository, never()).deleteById(anyLong());
	}
}