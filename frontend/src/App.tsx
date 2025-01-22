import React from "react";
import { Navbar } from "./components/Navbar.tsx";
import "./index.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage.tsx";
import LogInPage from "./pages/LogInPage.tsx";
import SignUpPage from "./pages/SignUpPage.tsx";
import NewPostPage from "./pages/NewPostPage.tsx";
import PostPage from "./pages/PostPage.tsx";
import Modal from "./components/Modal.tsx";

function App() {
  return (
    <div className="relative">
      <BrowserRouter>
        <Navbar />
        <Modal />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LogInPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/post/new" element={<NewPostPage />} />
          <Route path="/post/:id" element={<PostPage />} />
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
