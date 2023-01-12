import React, { Fragment, useState } from "react"
import Songs from "./components/Songs";
import Login from "./components/Login";
import LoginBox from "./components/LoginBox";
import RegisterBox from "./components/RegisterBox";
import { BrowserRouter, Route, Routes, Link } from 'react-router-dom';
import AuthService from "./services/auth.service";
import "bootstrap/dist/css/bootstrap.min.css";

class App extends React.PureComponent {

constructor(props) {
    super(props);

    this.state = {
      token: null,
      sub: null,
      isAdmin :false,
      isContentManager: false,
      isClient: true
    };
  }

  componentDidMount(){
    //authService.token.subscribe(x => this.setState({token:x}));
    console.log(this.state);
  }

  logout() {
    //authService.logout();
  }

  render(){
    const {token,sub,isAdmin,isContentManager,isClient} = this.state;

    return(
      <BrowserRouter>
          <div>
              {token &&
                <nav className="navbar navbar-expand navbar-dark bg-dark">
                  <div className="navbar-nav">
                    <Link to="/" className="nav-item nav-link">Home</Link>
                    <a onClick={this.logout} className="nav-item nav-link">Logout</a>
                  </div>
                </nav>
              }
              <div className="jumbotron">
                <div className="container">
                  <div className="row">
                    <div className="col-md-6 offset-md-3">
                      <Routes>
                        <Route exact path="/" component={LoginBox} />
                        <Route path="/login" component={RegisterBox} />
                      </Routes>

                    </div>
                  </div>
                </div>
              </div>
        </div>
      </BrowserRouter>
    //  <Login></Login>
    //   <BrowserRouter basename="/">
    //   <Routes>
    //     <Route exact path="/">
    //       <Login />
    //     </Route>
    //     <Route path="/home">
    //       <Home />
    //     </Route>
    //   </Routes>
    // </BrowserRouter>
    )
  }
    // showLoginBox() 
    // {
    //     this.setState({isLoginOpen: true, isRegisterOpen: false});
    // };
    
    // showRegisterBox() 
    // {
    //     this.setState({isRegisterOpen: true, isLoginOpen: false});
    // };

    // render() {
    //   return (
    //     <Fragment>
    //     <div className="box-controller">
    //         <div
    //             className={"controller " + (this.state.isLoginOpen
    //             ? "selected-controller"
    //             : "")}
    //             onClick={this
    //             .showLoginBox
    //             .bind(this)}>
    //             Login
    //         </div>
    //         <div
    //             className={"controller " + (this.state.isRegisterOpen
    //             ? "selected-controller"
    //             : "")}
    //             onClick={this
    //             .showRegisterBox
    //             .bind(this)}>
    //             Register
    //         </div>
    //     </div>

    //     <div className="box-container">
    //         {this.state.isLoginOpen && <LoginBox/>}
    //         {this.state.isRegisterOpen && <RegisterBox/>}
    //     </div>
    //     </Fragment>
    //   )
    //             }
  

}

export default App;