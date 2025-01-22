import React, { useRef, useState } from "react";
import axios from "axios";
import { useSelector } from "react-redux";
import { RootState } from "../store/store";
import { useDispatch } from "react-redux";
import { setPosts, setUpvotes } from "../store/postsSlice.ts";
import { Link, useNavigate } from "react-router-dom";
import { closeModal, openModal } from "../store/modalSlice.ts";
import Chatbot from "../components/Chatbot.tsx";

const HomePage = () => {
  const navigate = useNavigate();
  const subredditInputRef = useRef<HTMLInputElement>(null);

  const [error, setError] = useState<string | null>(null);
  const [updating, setUpdating] = useState(false);
  const [retrieving, setRetrieving] = useState(false);
  const [isChatbotDisplayed, setChatbotDisplay] = useState(false);

  const posts = useSelector((state: RootState) => state.posts.posts);
  const isUserLoggedIn = useSelector(
    (state: RootState) => state.auth.isUserLoggedIn
  );
  const user = useSelector((state: RootState) => state.auth.user);
  const upvotes = useSelector((state: RootState) => state.posts.upvotes);
  const dispatch = useDispatch();

  const clearInput = () => {
    if (subredditInputRef.current) {
      subredditInputRef.current.value = "";
    }
  };

  const fetchPosts = async () => {
    setError(null);
    console.log("fetchPosts() executed");
    const subreddit = subredditInputRef.current?.value;

    clearInput();

    if (!subreddit || subreddit.trim() === "") {
      setError("Enter a valid subreddit name");
      return;
    }

    try {
      setUpdating(true);

      await axios.get(`http://localhost:8081/updater/${subreddit}`);

      setUpdating(false);
      setRetrieving(true);

      let response = await axios.get("http://localhost:8080/posts");
      dispatch(setPosts(response.data));

      response = await axios.get("http://localhost:8080/upvotes");
      dispatch(setUpvotes(response.data));

      setError(null);
    } catch (err: any) {
      const errorMessage = String(err);
      setError(errorMessage);
      setUpdating(false);
    } finally {
      setRetrieving(false);
    }
  };

  const handleDeletePost = (postId: number) => {
    const deletePost = async () => {
      try {
        await fetch(`http://localhost:8080/posts/${postId}`, {
          method: "DELETE",
        });

        dispatch(setPosts([...posts.filter((post) => post.id !== postId)]));
        dispatch(closeModal());
      } catch (error) {
        console.error("Error occurred while deleting a post. Error: " + error);
      }
    };

    dispatch(
      openModal({
        title: "Are you sure to delete this post?",
        content: (
          <div className="flex justify-between mt-[20px]">
            <button
              onClick={() => dispatch(closeModal())}
              className="border-black px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Cancel
            </button>
            <button
              onClick={deletePost}
              className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Delete
            </button>
          </div>
        ),
      })
    );
  };

  const handleUpvotePost = async (postId: number) => {
    const userId = user?.id;

    if (!isUserLoggedIn) {
      navigate("/login");
      return;
    }

    try {
      const response = await axios.get(
        `http://localhost:8080/upvotes?postId=${postId}&userId=${userId}`
      );
      console.log(response.data);
      const existingUpvote = response.data;

      await axios.delete(`http://localhost:8080/upvotes/${existingUpvote.id}`);

      dispatch(
        setUpvotes(
          upvotes.filter(
            (upvote) => !(upvote.postId === postId && upvote.userId === userId)
          )
        )
      );
    } catch (error) {
      if (error.response && error.response.status === 404) {
        try {
          const newUpvote = {
            postId,
            userId,
            createdAt: new Date().toISOString(),
          };

          const postResponse = await axios.post(
            "http://localhost:8080/upvotes",
            newUpvote
          );

          dispatch(setUpvotes([...upvotes, postResponse.data]));
        } catch (postError) {
          console.error(
            "Error occurred while creating a new upvote. Error:",
            postError
          );
        }
      } else {
        console.error("Error occurred while managing upvote. Error:", error);
      }
    }
  };

  const getPostUpvotesCount = (postId: number) => {
    let count = 0;
    for (const upvote of upvotes) {
      if (upvote.postId === postId) count++;
    }
    return count;
  };

  return (
    <div className="mt-[150px] w-full justify-center max-w-[600px] mx-auto flex p-4 flex-col relative">
      <div className="fixed bottom-0 left-0 w-full bg-[#ffffffdf] flex z-10">
        {isChatbotDisplayed && <Chatbot />}
        <button
          onClick={() => setChatbotDisplay((prevDisplay) => !prevDisplay)}
          className="mx-auto my-2 py-2 px-5 border rounded-md border-black duration-300 hover:opacity-50 animate-show"
        >
          Chatbot
        </button>
      </div>

      <div className="border rounded-md border-black overflow-hidden flex animate-show">
        <input
          ref={subredditInputRef}
          placeholder="Search for subreddit"
          className="input"
        />
        <button
          onClick={fetchPosts}
          className="text-white border-l border-black bg-[#0309c5] px-7 hover:opacity-80 duration-300"
        >
          Fetch
        </button>
      </div>

      {error && <p className="text-red-600 mx-auto my-4 animate-show">{error}</p>}

      {updating ? (
        <p className="mx-auto my-4 animate-show">Updating database...</p>
      ) : (
        retrieving && <p className="mx-auto my-4 animate-show">Retrieving posts...</p>
      )}

      {isUserLoggedIn && (
        <Link
          to="/post/new"
          className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border mt-[20px] mx-auto mb-4 flex animate-show"
        >
          Add Post
        </Link>
      )}

      <div className="mt-[50px]">
        {posts.length === 0 && (
          <p className="text-center text-[#6e6e6e] animate-show">
            There are not any posts at the moment.
          </p>
        )}
        <ul className="flex flex-col gap-8 mb-[50px]">
          {posts.map((post: any, index: number) => (
            <li className="relative">
              <Link to={"/post/" + post.id} key={post.id}>
                <div className="border rounded-md border-black p-4 hover:opacity-60 duration-300 animate-show">
                  <h3 className="font-bold animate-show">{post.title}</h3>
                  <p className="mt-3 text-[14px] text-[#6d6d6d] animate-show">
                    {post.content.length === 0
                      ? "No content"
                      : post.content.length < 300
                      ? post.content
                      : post.content.slice(0, 300) + "..."}
                  </p>
                </div>
              </Link>

              <div className="absolute -top-[21px] left-0 text-[14px] text-[#525252] animate-show">
                {post.createdAt.slice(0, 10)}
              </div>

              {isUserLoggedIn && (
                <button
                  onClick={() => handleDeletePost(post.id)}
                  className="absolute top-1 right-1 flex animate-show"
                >
                  <img
                    src={"/assets/images/delete.svg"}
                    alt="Delete Icon"
                    className="animate-show"
                    width={20}
                  />
                </button>
              )}

              <button
                onClick={() => handleUpvotePost(post.id)}
                className="absolute right-2 bottom-2 flex hover:opacity-50 duration-300 rounded-md p-1 animate-show"
              >
                <img
                  src={"/assets/images/upvote.svg"}
                  alt="Upvote Icon"
                  width={20}
                />
                <p className="font-bold text-[14px]">
                  {getPostUpvotesCount(post.id)}
                </p>
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default HomePage;
