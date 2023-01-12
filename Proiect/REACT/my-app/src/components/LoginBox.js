import React, { Component } from 'react'

class LoginBox extends Component {
  render() {
    return (
      <form>
        <h3>Sign In</h3>
        <div className="mb-3">
          <label>Email address</label>
          <input
            type="email"
            className="form-control"
            placeholder="Enter email"
          />
        </div>
        <div className="mb-3">
          <label>Password</label>
          <input
            type="password"
            className="form-control"
            placeholder="Enter password"
          />
        </div>
        <div className="d-grid">
          <button type="submit" className="btn btn-primary">
            Submit
          </button>
        </div>
      </form>
    )
  }
}

// //Login Box
// class LoginBox extends React.Component {

//     constructor(props) {
//       super(props);
//       this.state = {};
//     }
  
//     submitLogin(e) {}



  
//     // render() {
//     //   return (
//     //     <div className="inner-container">
//     //       <div className="header">
//     //         Login
//     //       </div>
//     //       <div className="box">
  
//     //         <div className="input-group">
//     //           <label htmlFor="username">Username</label>
//     //           <input
//     //             type="text"
//     //             name="username"
//     //             className="login-input"
//     //             placeholder="Username"/>
//     //         </div>
  
//     //         <div className="input-group">
//     //           <label htmlFor="password">Password</label>
//     //           <input
//     //             type="password"
//     //             name="password"
//     //             className="login-input"
//     //             placeholder="Password"/>
//     //         </div>
  
//     //         <button
//     //           type="button"
//     //           className="login-btn"
//     //           onClick={this
//     //           .submitLogin
//     //           .bind(this)}>Login</button>
//     //       </div>
//     //     </div>
//     //   );
//     // }
  
//   }

  export default LoginBox;