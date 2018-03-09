package com.springuni.hermes.domain.link;

import static com.springuni.hermes.domain.Mocks.USER_ID;
import static com.springuni.hermes.domain.Mocks.createLinkSet;
import static com.springuni.hermes.domain.Mocks.createStandaloneLink;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class LinkRepositoryTest {

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private LinkSetRepository linkSetRepository;

    @Autowired
    private StandaloneLinkRepository standaloneLinkRepository;

    private EmbeddedLink embeddedLink;
    private StandaloneLink standaloneLink;

    @Before
    public void setUp() throws Exception {
        LinkSet linkSet = linkSetRepository.save(createLinkSet());
        embeddedLink = linkSet.getEmbeddedLinks().get(0);
        standaloneLink = standaloneLinkRepository.save(createStandaloneLink());
    }

    @Test
    public void findByUserId() {
        List<Link> links = linkRepository.findByUserId(USER_ID);
        assertThat(links, contains(embeddedLink, standaloneLink));
    }

}
