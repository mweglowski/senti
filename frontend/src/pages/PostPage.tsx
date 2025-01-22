import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { RootState } from "../store/store";
import { closeModal, openModal } from "../store/modalSlice.ts";
import axios from "axios";
import { setUpvotes } from "../store/postsSlice.ts";

const PostPage = () => {
  const { id } = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [post, setPost] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [comments, setComments] = useState<any>([]);
  const [isModalOpen, setModalOpen] = useState(false);
  const [newCommentContent, setNewCommentContent] = useState("");

  const isUserLoggedIn = useSelector(
    (state: RootState) => state.auth.isUserLoggedIn
  );
  const user = useSelector((state: RootState) => state.auth.user);
  const upvotes = useSelector((state: RootState) => state.posts.upvotes);

  useEffect(() => {
    const fetchPostAndComments = async () => {
      try {
        setLoading(true);

        let response = await fetch(`http://localhost:8080/posts/${id}`);
        setPost(await response.json());

        response = await fetch(`http://localhost:8080/comments?postId=${id}`);
        const commentsData = await response.json();

        const commentsWithAuthor = await Promise.all(
          commentsData.map(async (comment: any) => {
            const userResponse = await fetch(
              `http://localhost:8080/users/${comment.authorId}`
            );
            const userData = await userResponse.json();

            return {
              ...comment,
              username: userData.username,
            };
          })
        );

        setComments(commentsWithAuthor);
      } catch (err: any) {
        console.error("Failed to fetch post or comments.", err);
      } finally {
        setLoading(false);
      }
    };

    if (id) fetchPostAndComments();
  }, [id]);

  const handleAddComment = async () => {
    if (!newCommentContent.trim()) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/comments`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          postId: id,
          authorId: user!.id,
          content: newCommentContent,
          createdAt: new Date().toISOString(),
        }),
      });

      const newComment = await response.json();

      setComments((prevComments) => [
        ...prevComments,
        { ...newComment, username: user!.username },
      ]);

      setNewCommentContent("");
      setModalOpen(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteComment = (commentId: number) => {
    const deleteComment = async () => {
      try {
        await fetch(`http://localhost:8080/comments/${commentId}`, {
          method: "DELETE",
        });

        setComments([
          ...comments.filter((comment) => comment.id !== commentId),
        ]);
        dispatch(closeModal());
      } catch (error) {
        console.error("Error occurred while deleting a post. Error: " + error);
      }
    };

    dispatch(
      openModal({
        title: "Are you sure to delete this comment?",
        content: (
          <div className="flex justify-between mt-[20px]">
            <button
              onClick={() => dispatch(closeModal())}
              className="border-black px-6 rounded-md duration-300 hover:opacity-80 py-3 border"
            >
              Cancel
            </button>
            <button
              onClick={deleteComment}
              className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border"
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

  if (loading) {
    return <p className="text-center mt-10">Loading...</p>;
  }

  return (
    <div className="mt-[80px] w-full max-w-[600px] mx-auto p-4">
      <div className="text-[14px] text-[#525252] mb-[20px]">
        {post.createdAt?.slice(0, 10) || "Unknown Date"}
      </div>
      <div className="mb-[20px]">
        <h3 className="font-bold text-xl mb-[20px]">{post.title}</h3>
        <p className="mt-3 text-[14px] text-[#545454] hyphens-auto">
          {post.content && post.content.length > 0
            ? post.content
            : "No content"}
        </p>
        <button
          onClick={() => handleUpvotePost(post.id)}
          className="mt-[30px] mb-[10px] flex hover:opacity-50 duration-300 rounded-md p-1"
        >
          <img src={"/assets/images/upvote.svg"} alt="Upvote Icon" width={20} />
          <p className="font-bold text-[14px]">
            {getPostUpvotesCount(post.id)}
          </p>
        </button>
      </div>

      {isUserLoggedIn && (
        <button
          onClick={() => setModalOpen(true)}
          className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border mt-[20px] mx-auto mb-4 flex"
        >
          Add Comment
        </button>
      )}

      {comments && comments.length > 0 ? (
        <div>
          <ul className="flex flex-col gap-6">
            {comments.map((comment, index) => (
              <li key={index} className="flex flex-col rounded-md relative">
                <p className="text-sm text-gray-500">
                  {comment.createdAt.slice(0, 10)}
                </p>
                <p className="text-sm text-gray-700 font-bold">
                  {comment.username || "Unknown Author"}
                </p>
                <p className="text-[14px] text-gray-600">
                  {comment.content || "No content provided."}
                </p>

                {isUserLoggedIn && (
                  <button
                    onClick={() => handleDeleteComment(comment.id)}
                    className="absolute top-1 right-1 flex"
                  >
                    <img
                      src={"/assets/images/delete.svg"}
                      alt="Delete Icon"
                      width={20}
                    />
                  </button>
                )}
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <p className="text-gray-500 mt-4">This post has no comments.</p>
      )}

      {isModalOpen && (
        <div
          className="fixed inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center"
          onClick={() => setModalOpen(false)}
        >
          <div
            className="bg-white p-4 rounded-md max-w-[400px] w-full m-4"
            onClick={(e) => e.stopPropagation()}
          >
            <textarea
              value={newCommentContent}
              onChange={(e) => setNewCommentContent(e.target.value)}
              placeholder="Write your comment here..."
              className="input border border-black rounded-md"
              rows={4}
            />
            <div className="flex justify-between mt-2">
              <button
                onClick={() => setModalOpen(false)}
                className="border border-black px-6 rounded-md duration-300 hover:opacity-80"
              >
                Cancel
              </button>
              <button
                onClick={handleAddComment}
                className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PostPage;
