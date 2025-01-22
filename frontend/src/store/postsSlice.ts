import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface Post {
  id: number;
  authorId: number;
  content: string;
  createdAt: Date;
}

interface Upvote {
  id: number;
  userId: number;
  postId: number;
}

interface PostsState {
  posts: Post[];
  upvotes: Upvote[];
}

const initialState: PostsState = {
  posts: [],
  upvotes: []
};

const postsSlice = createSlice({
  name: "posts",
  initialState,
  reducers: {
    setPosts(state, action: PayloadAction<Post[]>) {
      state.posts = action.payload;
    },
    setUpvotes(state, action: PayloadAction<Upvote[]>) {
      state.upvotes = action.payload;
    },
  },
});

export const { setPosts, setUpvotes } = postsSlice.actions;

export default postsSlice.reducer;