import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './blog-post.reducer';
import { IBlogPost } from 'app/shared/model/blog-post.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IBlogPostUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface IBlogPostUpdateState {
  isNew: boolean;
  userId: string;
}

export class BlogPostUpdate extends React.Component<IBlogPostUpdateProps, IBlogPostUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      userId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (!this.state.isNew) {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getUsers();
  }

  saveEntity = (event, errors, values) => {
    values.entryTimestamp = convertDateTimeToServer(values.entryTimestamp);

    if (errors.length === 0) {
      const { blogPostEntity } = this.props;
      const entity = {
        ...blogPostEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/blog-post');
  };

  render() {
    const { blogPostEntity, users, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="jhipsterApp.blogPost.home.createOrEditLabel">Create or edit a BlogPost</h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : blogPostEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="id">ID</Label>
                    <AvInput id="blog-post-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="textLabel" for="text">
                    Text
                  </Label>
                  <AvField
                    id="blog-post-text"
                    type="text"
                    name="text"
                    validate={{
                      required: { value: true, errorMessage: 'This field is required.' },
                      minLength: { value: 100, errorMessage: 'This field is required to be at least 100 characters.' }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="entryTimestampLabel" for="entryTimestamp">
                    Entry Timestamp
                  </Label>
                  <AvInput
                    id="blog-post-entryTimestamp"
                    type="datetime-local"
                    className="form-control"
                    name="entryTimestamp"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.blogPostEntity.entryTimestamp)}
                    validate={{
                      required: { value: true, errorMessage: 'This field is required.' }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label for="user.login">User</Label>
                  <AvInput id="blog-post-user" type="select" className="form-control" name="user.id">
                    <option value="" key="0" />
                    {users
                      ? users.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.login}
                          </option>
                        ))
                      : null}
                  </AvInput>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/blog-post" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">Back</span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp; Save
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  users: storeState.userManagement.users,
  blogPostEntity: storeState.blogPost.entity,
  loading: storeState.blogPost.loading,
  updating: storeState.blogPost.updating,
  updateSuccess: storeState.blogPost.updateSuccess
});

const mapDispatchToProps = {
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(BlogPostUpdate);
