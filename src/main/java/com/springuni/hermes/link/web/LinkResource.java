package com.springuni.hermes.link.web;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springuni.hermes.link.model.Link;
import com.springuni.hermes.utm.model.UtmParameters;
import java.util.Optional;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.core.Relation;

@Getter
@Setter
@NoArgsConstructor
@Relation(value = "link", collectionRelation = "links")
public class LinkResource extends LinkBaseResource {

    @JsonProperty("utmParameters")
    private UtmParametersResource utmParametersResource;
    private String path;
    private String targetUrl;

    public LinkResource(Link link) {
        super(link);

        utmParametersResource =
                link.getUtmParameters().map(UtmParametersResource::new).orElse(null);

        path = link.getPath();
        targetUrl = link.getTargetUrl().toString();
    }

    LinkResource(String longUrl, UtmParameters utmParameters) {
        super(longUrl);
        this.utmParametersResource = new UtmParametersResource(utmParameters);
    }

    @JsonIgnore
    public UtmParameters getUtmParameters() {
        return Optional.ofNullable(utmParametersResource)
                .map(it -> new UtmParameters(it.getUtmSource(), it.getUtmMedium(),
                        it.getUtmCampaign(), it.getUtmTerm(), it.getUtmContent())).orElse(null);
    }

    @Data
    @NoArgsConstructor
    public class UtmParametersResource {

        private String utmSource;
        private String utmMedium;
        private String utmCampaign;
        private String utmTerm;
        private String utmContent;

        public UtmParametersResource(UtmParameters utmParameters) {
            utmSource = utmParameters.getUtmSource();
            utmMedium = utmParameters.getUtmMedium();
            utmCampaign = utmParameters.getUtmCampaign();
            utmParameters.getUtmContent().ifPresent(this::setUtmContent);
            utmParameters.getUtmTerm().ifPresent(this::setUtmTerm);
        }

    }

}
