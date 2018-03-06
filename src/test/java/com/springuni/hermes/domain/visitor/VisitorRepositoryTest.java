package com.springuni.hermes.domain.visitor;

import com.springuni.hermes.core.BaseRepositoryTest;

public class VisitorRepositoryTest extends BaseRepositoryTest<Visitor, Long, VisitorRepository> {

    @Override
    protected Visitor createEntity() {
        return new Visitor();
    }

}
