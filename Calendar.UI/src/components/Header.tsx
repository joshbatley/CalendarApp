import { Box, ThemeSwitch } from "@edgmont-ui/react";
import { Title } from "./Title";

export const Header: React.FC = () => (
  <Box display="flex" justifyContent="space-between" px="3" py="5" borderBottom="border.1" bg="offsetBackground">
    <Title />
    <ThemeSwitch />
  </Box>
)