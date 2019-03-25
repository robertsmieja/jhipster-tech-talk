import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './blog-post.reducer';
import { IBlogPost } from 'app/shared/model/blog-post.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IBlogPostDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class BlogPostDetail extends React.Component<IBlogPostDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { blogPostEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            BlogPost [<b>{blogPostEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="text">Text</span>
            </dt>
            <dd>{blogPostEntity.text}</dd>
            <dt>
              <span id="entryTimestamp">Entry Timestamp</span>
            </dt>
            <dd>
              <TextFormat value={blogPostEntity.entryTimestamp} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>User</dt>
            <dd>{blogPostEntity.user ? blogPostEntity.user.login : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/blog-post" replace color="info">
            <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/blog-post/${blogPostEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ blogPost }: IRootState) => ({
  blogPostEntity: blogPost.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(BlogPostDetail);
