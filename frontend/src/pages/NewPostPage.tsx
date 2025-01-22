import React, { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../store/store";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { setPosts } from "../store/postsSlice.ts";

const NewPostPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const titleInputRef = useRef<HTMLInputElement>(null);
  const contentInputRef = useRef<HTMLTextAreaElement>(null);
  const [error, setError] = useState("");

  const isUserLoggedIn = useSelector(
    (state: RootState) => state.auth.isUserLoggedIn
  );
  const user = useSelector((state: RootState) => state.auth.user);
  const posts = useSelector((state: RootState) => state.posts.posts);

  useEffect(() => {
    if (!isUserLoggedIn) {
      navigate("/");
    }
  });

  const handleAddPost = async () => {
    const title = titleInputRef.current!.value;
    const content = contentInputRef.current!.value;

    if (title.trim().length === 0 || content.trim().length === 0) {
      setError("Both fields should not be empty.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/posts`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          authorId: user!.id,
          title,
          content,
          createdAt: new Date().toISOString(),
        }),
      });

      const createdPost = await response.json();
      dispatch(setPosts([...posts, createdPost]));

      navigate("/");
    } catch (error) {
      console.error("Error occurred while creating a post. Error: " + error);
    }
  };

  return (
    <div className="mt-[150px] flex flex-col">
      <p
        className={
          "text-red-600 text-center h-[30px] animate-show " + (error === "" && "opacity-0")
        }
      >
        {error}
      </p>

      <div className="max-w-[550px] mx-auto p-4 flex flex-col gap-2 w-full">
        <input
          ref={titleInputRef}
          placeholder="Title"
          className="input border border-black rounded-md animate-show"
        />
        <textarea
          ref={contentInputRef}
          placeholder="Content"
          className="input border border-black rounded-md animate-show"
          rows={8}
        />
      </div>

      <button
        onClick={handleAddPost}
        className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border mt-[20px] mx-auto animate-show"
      >
        Confirm
      </button>
    </div>
  );
};

export default NewPostPage;
