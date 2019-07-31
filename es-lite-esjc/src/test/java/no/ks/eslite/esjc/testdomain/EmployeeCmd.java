package no.ks.eslite.esjc.testdomain;

import no.ks.eslite.framework.Command;

public abstract class EmployeeCmd implements Command<Employee> {

    @Override
    public Employee getAggregate() {
        return Employee.builder().build();
    }
}
